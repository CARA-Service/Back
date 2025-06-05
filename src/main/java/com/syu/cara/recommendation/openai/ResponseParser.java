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
            // JSON 응답이 error를 포함하는 경우 처리
            if (gptJson.contains("error")) {
                // 에러 메시지를 처리하거나 로깅할 수 있음
                System.out.println("Error in response: " + gptJson);
            }
            // GPT 응답이 배열로 시작하는지 확인
            if (gptJson.startsWith("[")) {
                return objectMapper.readValue(gptJson, new TypeReference<List<RecommendationResponse>>() {});
            }
            // GPT 응답이 단일 객체인 경우 처리
            else {
                RecommendationResponse response = objectMapper.readValue(gptJson, RecommendationResponse.class);
                return List.of(response);  // 단일 객체를 리스트로 변환
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("GPT 응답 파싱 실패", e);
        }
    }
}