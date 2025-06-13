package com.syu.cara.recommendation.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseParser {
        private final ObjectMapper mapper = new ObjectMapper();

        public List<RecommendationResponse> parse(String gptRawResponse) {
            try {
                JsonNode root = mapper.readTree(gptRawResponse);
                String content = root.get("choices").get(0).get("message").get("content").asText();

                // GPT 응답 content가 JSON 문자열이므로 다시 파싱
                return mapper.readValue(content, new TypeReference<List<RecommendationResponse>>() {});
            } catch (Exception e) {
                throw new RuntimeException("GPT 응답 파싱 실패", e);
            }
        }
    }
