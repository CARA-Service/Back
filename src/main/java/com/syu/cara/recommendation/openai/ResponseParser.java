package com.syu.cara.recommendation.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.recommendation.dto.GPTRecommendationBundle;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import com.syu.cara.recommendation.dto.RentalCondition;
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

//        public RentalCondition parseToCondition(String gptRawResponse) {
//            try {
//                JsonNode root = mapper.readTree(gptRawResponse);
//                String content = root.get("choices").get(0).get("message").get("content").asText();
//
//                // GPT가 JSON으로 파싱 가능한 구조화 응답을 줬다고 가정
//                return mapper.readValue(content, RentalCondition.class);
//            } catch (Exception e) {
//                throw new RuntimeException("GPT 응답 파싱 실패 (RentalCondition)", e);
//            }
//        }

    public GPTRecommendationBundle parseToConditionBundle(String gptRawResponse) {
        try {
            JsonNode root = mapper.readTree(gptRawResponse);
            String content = root.get("choices").get(0).get("message").get("content").asText();
            JsonNode parsed = mapper.readTree(content);

            RentalCondition condition = mapper.treeToValue(parsed, RentalCondition.class);
            JsonNode messageNode = parsed.get("naturalLanguageMessage");

            String message = (messageNode != null && !messageNode.isNull()) ?
                    messageNode.asText() :
                    "조건에 맞는 차량입니다.";  // fallback 메시지

            GPTRecommendationBundle bundle = new GPTRecommendationBundle();
            bundle.setCondition(condition);
            bundle.setNaturalLanguageMessage(message);
            return bundle;
        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 파싱 실패 (Bundle)", e);
        }
    }

}
