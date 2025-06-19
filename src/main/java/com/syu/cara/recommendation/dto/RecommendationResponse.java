package com.syu.cara.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

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
    private BigDecimal totalPrice;
    private BigDecimal basePrice;
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
        response.setTotalPrice(BigDecimal.ZERO);
        response.setBasePrice(BigDecimal.ZERO);
        response.setAgencyName("");
        return response;
    }
}
