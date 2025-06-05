package com.syu.cara.recommendation.service;

import com.syu.cara.common.classifier.DomainClassifier;
import com.syu.cara.recommendation.dto.RecommendationRequest;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import com.syu.cara.recommendation.openai.OpenAIClient;
import com.syu.cara.recommendation.openai.PromptBuilder;
import com.syu.cara.recommendation.openai.ResponseParser;
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
    private final DomainClassifier domainClassifier; // ✅ 주입 받아야 함

    public List<RecommendationResponse> generateRecommendation(RecommendationRequest request) {

        // ✅ 추천 요청에서 사용자 의도를 간략히 문자열로 추출
        String userInput = buildNaturalInput(request);

        if (!domainClassifier.isRentalDomain(userInput)) {
            return List.of(RecommendationResponse.systemMessage(
                    "죄송합니다. 현재는 렌터카 관련 질문만 도와드릴 수 있습니다."));
        }

        String prompt = promptBuilder.buildPrompt(request);
        String response = openAIClient.chat(prompt);
//        promptHistoryRepository.save(PromptHistory.of(request, prompt, response));
        return responseParser.parse(response);
    }

    // ✅ 추천 요청의 주요 정보를 자연어처럼 조합
    private String buildNaturalInput(RecommendationRequest request) {
        return String.format("목적: %s, 추가 옵션: %s",
                request.getPurpose() != null ? request.getPurpose() : "",
                request.getAdditionalOptions() != null ? request.getAdditionalOptions() : "");
    }
}
