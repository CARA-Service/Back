package com.syu.cara.recommendation.dto;

import lombok.Data;

@Data
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

}