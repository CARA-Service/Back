package com.syu.cara.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationResponse {
    private Long recommendationId;
    private Long carId;
    private String modelName;

    private String fuelType;
    private double fuelEfficiency;
    private int capacity;
    private String luggageSize;

    private int totalPrice;
    private int basePrice;

    private String agencyName;


    public static RecommendationResponse systemMessage(String message) {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendationId(0L);
        response.setCarId(0L);
        response.setModelName(message);
        response.setFuelType("");
        response.setFuelEfficiency(0.0);
        response.setCapacity(0);
        response.setLuggageSize("");
        response.setTotalPrice(0);
        response.setBasePrice(0);
        response.setAgencyName("");
        return response;
    }


//    @JsonProperty("error")
//    private ErrorResponse error;  // 에러 필드를 추가할 수 있음
//
//    // ErrorResponse 클래스 정의
//    public static class ErrorResponse {
//        private String message;
//        private String type;
//    }

}