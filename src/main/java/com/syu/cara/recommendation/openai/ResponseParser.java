package com.syu.cara.recommendation.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<RecommendationResponse> parse(String gptJson) {
        try {
            return objectMapper.readValue(
                    gptJson,
                    new TypeReference<>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("GPT 응답 파싱 실패", e);
        }
    }
}