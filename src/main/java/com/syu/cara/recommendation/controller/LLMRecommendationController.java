package com.syu.cara.recommendation.controller;

import com.syu.cara.recommendation.dto.RecommendationRequest;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import com.syu.cara.recommendation.service.LLMRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LLMRecommendationController {

    private final LLMRecommendationService recommendationService;

    @PostMapping("/recommendations")
    public ResponseEntity<List<RecommendationResponse>> recommend(
            @RequestBody RecommendationRequest request) {

        List<RecommendationResponse> result = recommendationService.generateRecommendation(request);
        return ResponseEntity.ok(result);
    }
}
