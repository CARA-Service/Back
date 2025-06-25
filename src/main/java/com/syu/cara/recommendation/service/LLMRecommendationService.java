package com.syu.cara.recommendation.service;

import com.syu.cara.car.domain.Car;
import com.syu.cara.common.classifier.DomainClassifier;
import com.syu.cara.recommendation.domain.Recommendation;
import com.syu.cara.recommendation.dto.GPTRecommendationBundle;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import com.syu.cara.recommendation.dto.RentalCondition;
import com.syu.cara.recommendation.openai.OpenAIClient;
import com.syu.cara.recommendation.openai.PromptBuilder;
import com.syu.cara.recommendation.openai.ResponseParser;
import com.syu.cara.car.repository.CarRepository;
import com.syu.cara.recommendation.repository.RecommendationRepository;
import com.syu.cara.rentalrequest.domain.RentalRequest;
import com.syu.cara.rentalrequest.domain.PromptHistory;
import com.syu.cara.rentalrequest.repository.RentalRequestRepository;
import com.syu.cara.rentalrequest.repository.PromptHistoryRepository;

import com.syu.cara.user.domain.User;
import com.syu.cara.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class LLMRecommendationService {

    private final PromptBuilder promptBuilder;
    private final OpenAIClient openAIClient;
    private final ResponseParser responseParser;
    private final RecommendationRepository recommendationRepository;
    private final RentalRequestRepository rentalRequestRepository;
    private final PromptHistoryRepository promptHistoryRepository;
    private final DomainClassifier domainClassifier;
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public List<RecommendationResponse> generateRecommendation(String userInput, User user) {

        if (!domainClassifier.isRentalDomain(userInput)) {
            return List.of(RecommendationResponse.systemMessage(
                    "죄송합니다. 현재는 렌터카 관련 질문만 도와드릴 수 있습니다."));
        }

        // 1. RentalRequest 생성 및 저장
        GPTRecommendationBundle bundle = getRecommendationBundle(userInput);
        RentalCondition condition = bundle.getCondition();
        String gptMessage = bundle.getNaturalLanguageMessage();

        // RentalRequest 엔티티 생성 (사용자 정보 포함)
        RentalRequest rentalRequest = createRentalRequest(condition, userInput, user);
        RentalRequest savedRentalRequest = rentalRequestRepository.save(rentalRequest);

        // 2. PromptHistory 저장 (사용자 입력과 GPT 응답, 사용자 정보 포함)
        PromptHistory promptHistory = PromptHistory.builder()
                .rentalRequest(savedRentalRequest)
                .user(user)  // 사용자 정보 추가
                .userInput(userInput)
                .botResponse(gptMessage)
                .timestamp(LocalDateTime.now())
                .build();
        promptHistoryRepository.save(promptHistory);

        // 날짜 파싱
        LocalDate rentalDate = parseDate(condition.getRentalDate());
        LocalDate returnDate = parseDate(condition.getReturnDate());
        
        // 날짜가 유효하지 않은 경우 기본값 설정
        if (rentalDate == null) {
            rentalDate = LocalDate.now().plusDays(1);
        }
        if (returnDate == null) {
            returnDate = rentalDate.plusDays(3);
        }
        
        // 만약 반납일이 대여일보다 이전이면 조정
        if (returnDate.isBefore(rentalDate)) {
            returnDate = rentalDate.plusDays(1);
        }
        
        System.out.println("🗓️ 대여 기간: " + rentalDate + " ~ " + returnDate);

        List<Car> candidates = carRepository.findAllWithAgency();
        // 해당 기간에 이미 예약된 차량 ID 목록 조회 (status가 "결제완료"인 예약만 고려)
        Set<Long> reservedCarIds = reservationRepository.findReservedCarIds(rentalDate, returnDate);
        System.out.println("🚫 이미 예약된 차량 수: " + reservedCarIds.size());
        
        //추천 조건
        System.out.println("🔍 필터링 시작 - 찾는 지역: " + condition.getPickupLocation());
        String locationKeyword = condition.getPickupLocation() != null ?
            condition.getPickupLocation().replace("도", "").replace("시", "").trim() : "";
        System.out.println("🔍 지역 키워드: '" + locationKeyword + "'");

        List<Car> filtered = candidates.stream()
                .filter(car -> {
                    // 1. 이미 예약된 차량 필터링 (가장 먼저 체크)
                    if (reservedCarIds.contains(car.getCarId())) {
                        System.out.println("🚫 예약 불가: " + car.getModelName() + " (이미 예약됨)");
                        return false;
                    }
                    
                    // 2. 지역 필터링
                    if (condition.getPickupLocation() == null) return true;

                    // 디버깅 로그
                    String agencyName = car.getAgency().getAgencyName();
                    String agencyLocation = car.getAgency().getLocation();
                    boolean matches = (agencyName != null && agencyName.contains(locationKeyword))
                            || (agencyLocation != null && agencyLocation.contains(locationKeyword));

                    // 매칭 실패한 경우도 로그 출력 (처음 5개만)
                    if (!matches && candidates.indexOf(car) < 5) {
                        System.out.println("❌ 매칭 실패: " + car.getModelName() + " - " + agencyName + " (키워드: '" + locationKeyword + "')");
                    }

                    if (matches) {
                        System.out.println("🎯 매칭된 차량: " + car.getModelName() + " - " + agencyName);
                    }

                    return matches;
                })
                .limit(5)
                .toList();

        System.out.println("후보 차량 수: " + candidates.size());
        System.out.println("필터링된 차량 수: " + filtered.size());
        filtered.forEach(car -> System.out.println(
                car.getModelName() + " | 연비: " + car.getFuelEfficiency() + " | 가격: " + car.getDailyPrice() + " | 인원: " + car.getCapacity()
        ));

        System.out.println("🚗 Parsed Condition from GPT:");
        System.out.println("  - 지역: " + condition.getPickupLocation());
        System.out.println("  - 인원: " + condition.getPassengerCount());
        System.out.println("  - 예산: " + condition.getBudget());
        System.out.println("  - 연비: " + condition.getFuelEfficiencyPreference());
        System.out.println(condition);

        // 3. 추천 결과를 DB에 저장하고 recommendation_id 생성
        return filtered.stream()
                .map(car -> {
                    // Recommendation 엔티티 생성 및 저장 (rentalRequest 연결)
                    Recommendation recommendation = Recommendation.builder()
                            .car(car)
                            .rentalRequest(savedRentalRequest)  // request_id 연결
                            .totalPrice(car.getDailyPrice())
                            .basePrice(car.getDailyPrice())
                            .createdAt(LocalDateTime.now())
                            .build();

                    // DB에 저장하여 recommendation_id 생성
                    Recommendation savedRecommendation = recommendationRepository.save(recommendation);

                    // RecommendationResponse 생성 시 recommendation_id 포함
                    RecommendationResponse response = new RecommendationResponse(car, gptMessage);
                    response.setRecommendationId(savedRecommendation.getRecommendationId());

                    return response;
                })
                .toList();
    }

    // GPT 응답 처리 (API 실패 시 기본값 반환)
    private GPTRecommendationBundle getRecommendationBundle(String userInput) {
        try {
            String prompt = promptBuilder.buildParsingPrompt(userInput);
            String response = openAIClient.chat(prompt);
            return responseParser.parseToConditionBundle(response);
        } catch (Exception e) {
            System.err.println("❌ OpenAI API 호출 실패, 기본 추천 로직 사용: " + e.getMessage());
            // OpenAI API 실패 시 기본 조건으로 추천
            GPTRecommendationBundle bundle = new GPTRecommendationBundle();
            bundle.setCondition(createDefaultCondition(userInput));
            bundle.setNaturalLanguageMessage("여행에 적합한 차량들을 준비했습니다! 🚗");
            return bundle;
        }
    }

    // OpenAI API 실패 시 기본 조건 생성
    private RentalCondition createDefaultCondition(String userInput) {
        RentalCondition condition = new RentalCondition();

        // 사용자 입력에서 지역 추출
        String extractedLocation = extractLocationFromUserInput(userInput);
        condition.setPickupLocation(extractedLocation);

        // 기본값 설정
        condition.setPassengerCount(4);
        condition.setLuggageSize("중형");
        condition.setFuelEfficiencyPreference(15.0);
        condition.setBudget(100000);
        condition.setPurpose("여행");

        // 사용자 입력에서 간단한 키워드 추출
        if (userInput.contains("가족") || userInput.contains("7인")) {
            condition.setPassengerCount(7);
            condition.setLuggageSize("대형");
        }
        if (userInput.contains("경제") || userInput.contains("저렴")) {
            condition.setBudget(50000);
        }
        if (userInput.contains("고급") || userInput.contains("럭셔리")) {
            condition.setBudget(200000);
        }

        System.out.println("🔧 기본 조건 생성 - 추출된 지역: " + extractedLocation);
        return condition;
    }

    // 사용자 입력에서 지역 추출
    private String extractLocationFromUserInput(String userInput) {
        // 서울 구 단위 지역들 (서울로 매핑)
        String[] seoulDistricts = {"강남", "강북", "강서", "강동", "관악", "광진", "구로", "금천", "노원",
                                  "도봉", "동대문", "동작", "마포", "서대문", "서초", "성동", "성북",
                                  "송파", "양천", "영등포", "용산", "은평", "종로", "중구", "중랑"};

        // 서울 구 단위 체크
        for (String district : seoulDistricts) {
            if (userInput.contains(district)) {
                System.out.println("🗺️ 서울 " + district + " 지역 인식 → 서울로 매핑");
                return "서울";
            }
        }

        // 광역시/도 단위 체크
        String[] locations = {"서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종",
                             "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주"};

        for (String location : locations) {
            if (userInput.contains(location)) {
                return location;
            }
        }

        // 제주도 처리
        if (userInput.contains("제주도")) {
            return "제주";
        }

        return "제주"; // 기본값
    }

    // RentalRequest 생성 메서드 (사용자 정보 포함)
    private RentalRequest 
  
  (RentalCondition condition, String userInput, User user) {
        return RentalRequest.builder()
                .user(user)  // 사용자 정보 설정
                .pickupLocation(condition.getPickupLocation() != null ? condition.getPickupLocation() : "제주")
                .pLatitude(33.4996) // 기본값 (제주도 좌표)
                .pLongitude(126.5312)
                .rentalDate(LocalDate.now().plusDays(1)) // 기본값: 내일
                .returnDate(LocalDate.now().plusDays(3)) // 기본값: 3일 후
                .purpose(condition.getPurpose() != null ? condition.getPurpose() : "여행")
                .passengerCount(condition.getPassengerCount() != null ? condition.getPassengerCount() : 4)
                .luggageSize(condition.getLuggageSize() != null ? condition.getLuggageSize() : "중형")
                .fuelEfficiencyPreference(condition.getFuelEfficiencyPreference() != null ?
                    BigDecimal.valueOf(condition.getFuelEfficiencyPreference()) : BigDecimal.valueOf(15.0))
                .budget(condition.getBudget() != null ? condition.getBudget() : 100000)
                .additionalOptions(extractAdditionalOptions(userInput))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 사용자 입력에서 추가 옵션 추출
    private String extractAdditionalOptions(String userInput) {
        StringBuilder options = new StringBuilder();

        if (userInput.contains("블루투스") || userInput.contains("bluetooth")) {
            options.append("블루투스,");
        }
        if (userInput.contains("네비게이션") || userInput.contains("네비")) {
            options.append("네비게이션,");
        }
        if (userInput.contains("후방카메라") || userInput.contains("후방센서")) {
            options.append("후방카메라,");
        }
        if (userInput.contains("자동주차")) {
            options.append("자동주차,");
        }

        String result = options.toString();
        return result.endsWith(",") ? result.substring(0, result.length() - 1) : result;

    // 날짜 문자열을 LocalDate로 파싱하는 메서드
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("날짜 형식이 잘못되었습니다: " + dateStr);
            return null;
        }

    }

}
