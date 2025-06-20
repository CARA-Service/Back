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

        String prompt = promptBuilder.buildParsingPrompt(userInput);
        String response = openAIClient.chat(prompt);

        if (!domainClassifier.isRentalDomain(userInput)) {
            return List.of(RecommendationResponse.systemMessage(
                    "죄송합니다. 현재는 렌터카 관련 질문만 도와드릴 수 있습니다."));
        }

        GPTRecommendationBundle bundle = responseParser.parseToConditionBundle(response);
        RentalCondition condition = bundle.getCondition();
        String gptMessage = bundle.getNaturalLanguageMessage();

        List<Car> candidates = carRepository.findAllWithAgency(); // → fetch join으로
        
        //추천 조건
        List<Car> filtered = candidates.stream()
                .filter(car -> {
                    if (condition.getPickupLocation() == null) return true;
                    String locationKeyword = condition.getPickupLocation().replace("도", "").replace("시", "").trim();
                    return (car.getAgency().getAgencyName() != null && car.getAgency().getAgencyName().contains(locationKeyword))
                            || (car.getAgency().getLocation() != null && car.getAgency().getLocation().contains(locationKeyword));
                })

                .limit(5)
                .toList();

        System.out.println("후보 차량 수: " + candidates.size());
        System.out.println("필터링된 차량 수: " + filtered.size());
        filtered.forEach(car -> System.out.println(
                car.getModelName() + " | 연비: " + car.getFuelEfficiency() + " | 가격: " + car.getDailyPrice() + " | 인원: " + car.getCapacity()
        ));

        System.out.println("🚗 Parsed Condition from GPT:");
        System.out.println(condition);

        return filtered.stream()
                .map(car -> new RecommendationResponse(car, gptMessage))
                .toList();
    }

}
