package com.syu.cara.recommendation.service;

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

    public List<RecommendationResponse> generateRecommendation(RecommendationRequest request) {
        String prompt = promptBuilder.buildPrompt(request);
        String response = openAIClient.chat(prompt);
//        promptHistoryRepository.save(PromptHistory.of(request, prompt, response));
        return responseParser.parse(response);
    }
}