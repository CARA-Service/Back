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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Car> candidates = carRepository.findAllWithAgency(); // → fetch join으로
        
        //추천 조건
        System.out.println("🔍 필터링 시작 - 찾는 지역: " + condition.getPickupLocation());

        // 영어 지역명을 한국어로 변환
        String originalLocation = condition.getPickupLocation();
        String koreanLocation = convertEnglishToKorean(originalLocation);

        String locationKeyword = koreanLocation != null ?
            koreanLocation.replace("도", "").replace("시", "").trim() : "";
        System.out.println("🔍 원본 지역: '" + originalLocation + "' → 한국어: '" + koreanLocation + "' → 키워드: '" + locationKeyword + "'");

        List<Car> filtered = candidates.stream()
                .filter(car -> {
                    if (condition.getPickupLocation() == null) return true;

                    // 디버깅 로그
                    String agencyName = car.getAgency().getAgencyName();
                    String agencyLocation = car.getAgency().getLocation();

                    // 여러 키워드로 검색 (원본, 한국어, 영어)
                    boolean matches = false;
                    String[] searchKeywords = {locationKeyword, originalLocation, koreanLocation};

                    for (String keyword : searchKeywords) {
                        if (keyword != null && !keyword.isEmpty()) {
                            if ((agencyName != null && agencyName.contains(keyword))
                                    || (agencyLocation != null && agencyLocation.contains(keyword))) {
                                matches = true;
                                System.out.println("🎯 매칭된 차량: " + car.getModelName() + " - " + agencyName + " (키워드: '" + keyword + "')");
                                break;
                            }
                        }
                    }

                    // 매칭 실패한 경우도 로그 출력 (처음 5개만)
                    if (!matches && candidates.indexOf(car) < 5) {
                        System.out.println("❌ 매칭 실패: " + car.getModelName() + " - " + agencyName + " (검색 키워드들: " + String.join(", ", searchKeywords) + ")");
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

        // 전국 시/군/구 매핑 (Map 사용)
        Map<String, String> cityMapping = new HashMap<>();

        // 부산광역시
        cityMapping.put("해운대", "부산"); cityMapping.put("서면", "부산");
        cityMapping.put("남포동", "부산"); cityMapping.put("광안리", "부산");

        // 대구광역시
        cityMapping.put("동성로", "대구"); cityMapping.put("수성구", "대구");

        // 인천광역시
        cityMapping.put("송도", "인천"); cityMapping.put("부평", "인천"); cityMapping.put("계양", "인천");

        // 광주광역시
        cityMapping.put("상무지구", "광주"); cityMapping.put("충장로", "광주");

        // 대전광역시
        cityMapping.put("둔산", "대전"); cityMapping.put("유성", "대전");

        // 울산광역시
        cityMapping.put("남구", "울산"); cityMapping.put("중구", "울산");

        // 경기도
        String[] gyeonggiCities = {"수원", "성남", "고양", "용인", "부천", "안산", "안양", "남양주",
                                  "화성", "평택", "의정부", "시흥", "파주", "광명", "김포", "군포",
                                  "광주", "이천", "양주", "오산", "구리", "안성", "포천", "의왕",
                                  "하남", "여주", "양평", "동두천", "과천", "가평", "연천","일산"};
        for (String city : gyeonggiCities) cityMapping.put(city, "경기");

        // 강원도
        String[] gangwonCities = {"춘천", "원주", "강릉", "동해", "태백", "속초", "삼척", "홍천",
                                 "횡성", "영월", "평창", "정선", "철원", "화천", "양구", "인제",
                                 "고성", "양양"};
        for (String city : gangwonCities) cityMapping.put(city, "강원");

        // 충청북도
        String[] chungbukCities = {"청주", "충주", "제천", "보은", "옥천", "영동", "증평", "진천",
                                  "괴산", "음성", "단양"};
        for (String city : chungbukCities) cityMapping.put(city, "충북");

        // 충청남도
        String[] chungnamCities = {"천안", "공주", "보령", "아산", "서산", "논산", "계룡", "당진",
                                  "금산", "부여", "서천", "청양", "홍성", "예산", "태안"};
        for (String city : chungnamCities) cityMapping.put(city, "충남");

        // 전라북도
        String[] jeonbukCities = {"전주", "군산", "익산", "정읍", "남원", "김제", "완주", "진안",
                                 "무주", "장수", "임실", "순창", "고창", "부안"};
        for (String city : jeonbukCities) cityMapping.put(city, "전북");

        // 전라남도
        String[] jeonnamCities = {"목포", "여수", "순천", "나주", "광양", "담양", "곡성", "구례",
                                 "고흥", "보성", "화순", "장흥", "강진", "해남", "영암", "무안",
                                 "함평", "영광", "장성", "완도", "진도", "신안"};
        for (String city : jeonnamCities) cityMapping.put(city, "전남");

        // 경상북도
        String[] gyeongbukCities = {"포항", "경주", "김천", "안동", "구미", "영주", "영천", "상주",
                                   "문경", "경산", "군위", "의성", "청송", "영양", "영덕", "청도",
                                   "고령", "성주", "칠곡", "예천", "봉화", "울진", "울릉"};
        for (String city : gyeongbukCities) cityMapping.put(city, "경북");

        // 경상남도
        String[] gyeongnamCities = {"창원", "진주", "통영", "사천", "김해", "밀양", "거제", "양산",
                                   "의령", "함안", "창녕", "고성", "남해", "하동", "산청", "함양",
                                   "거창", "합천"};
        for (String city : gyeongnamCities) cityMapping.put(city, "경남");

        // 제주특별자치도
        cityMapping.put("제주시", "제주"); cityMapping.put("서귀포", "제주");

        // 도시 매핑 체크
        for (Map.Entry<String, String> entry : cityMapping.entrySet()) {
            if (userInput.contains(entry.getKey())) {
                System.out.println("🗺️ " + entry.getKey() + " → " + entry.getValue() + " 매핑");
                return entry.getValue();
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

    // 영어 지역명을 한국어로 변환
    private String convertEnglishToKorean(String englishLocation) {
        if (englishLocation == null) return null;

        Map<String, String> englishToKorean = new HashMap<>();
        englishToKorean.put("Seoul", "서울");
        englishToKorean.put("Busan", "부산");
        englishToKorean.put("Daegu", "대구");
        englishToKorean.put("Incheon", "인천");
        englishToKorean.put("Gwangju", "광주");
        englishToKorean.put("Daejeon", "대전");
        englishToKorean.put("Ulsan", "울산");
        englishToKorean.put("Sejong", "세종");
        englishToKorean.put("Gyeonggi", "경기");
        englishToKorean.put("Gangwon", "강원");
        englishToKorean.put("Chungbuk", "충북");
        englishToKorean.put("Chungnam", "충남");
        englishToKorean.put("Jeonbuk", "전북");
        englishToKorean.put("Jeonnam", "전남");
        englishToKorean.put("Gyeongbuk", "경북");
        englishToKorean.put("Gyeongnam", "경남");
        englishToKorean.put("Jeju", "제주");

        // 영어 지역명이 있으면 한국어로 변환, 없으면 원본 반환
        return englishToKorean.getOrDefault(englishLocation, englishLocation);
    }

    // RentalRequest 생성 메서드 (사용자 정보 포함)
    private RentalRequest createRentalRequest(RentalCondition condition, String userInput, User user) {
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
    }

}
