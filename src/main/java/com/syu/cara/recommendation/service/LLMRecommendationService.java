package com.syu.cara.recommendation.service;

import com.syu.cara.car.domain.Car;
import com.syu.cara.common.classifier.DomainClassifier;
import com.syu.cara.recommendation.dto.GPTRecommendationBundle;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import com.syu.cara.recommendation.dto.RentalCondition;
import com.syu.cara.recommendation.openai.OpenAIClient;
import com.syu.cara.recommendation.openai.PromptBuilder;
import com.syu.cara.recommendation.openai.ResponseParser;
import com.syu.cara.car.repository.CarRepository;
import com.syu.cara.recommendation.repository.RecommendationRepository;
import com.syu.cara.rentalrequest.repository.PromptHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMRecommendationService {

    private final PromptBuilder promptBuilder;
    private final OpenAIClient openAIClient;
    private final ResponseParser responseParser;
    private final RecommendationRepository recommendationRepository;
    private final PromptHistoryRepository promptHistoryRepository;
    private final DomainClassifier domainClassifier;
    private final CarRepository carRepository;

    public List<RecommendationResponse> generateRecommendation(String userInput) {

        if (!domainClassifier.isRentalDomain(userInput)) {
            return List.of(RecommendationResponse.systemMessage(
                    "죄송합니다. 현재는 렌터카 관련 질문만 도와드릴 수 있습니다."));
        }

        // GPT 응답 처리
        GPTRecommendationBundle bundle = getRecommendationBundle(userInput);
        RentalCondition condition = bundle.getCondition();
        String gptMessage = bundle.getNaturalLanguageMessage();

        List<Car> candidates = carRepository.findAllWithAgency(); // → fetch join으로
        
        //추천 조건
        System.out.println("🔍 필터링 시작 - 찾는 지역: " + condition.getPickupLocation());
        String locationKeyword = condition.getPickupLocation() != null ?
            condition.getPickupLocation().replace("도", "").replace("시", "").trim() : "";
        System.out.println("🔍 지역 키워드: '" + locationKeyword + "'");

        List<Car> filtered = candidates.stream()
                .filter(car -> {
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

        return filtered.stream()
                .map(car -> new RecommendationResponse(car, gptMessage))
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
            bundle.setNaturalLanguageMessage("조건에 맞는 차량을 추천해드립니다.");
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

}
