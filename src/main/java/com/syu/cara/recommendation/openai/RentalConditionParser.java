package com.syu.cara.recommendation.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.cara.recommendation.dto.RentalCondition;

public class RentalConditionParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RentalCondition parse(String response) {
        try {
            return objectMapper.readValue(response, RentalCondition.class);
        } catch (Exception e) {
            throw new RuntimeException("조건 파싱 실패", e);
        }
    }
}
