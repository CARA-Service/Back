package com.syu.cara.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.syu.cara.car.domain.Car;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
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

    // ✅ GPT가 만들어준 자연어 설명 추가
    private String naturalLanguageMessage;
    public RecommendationResponse(Car car, String message) {
        this.recommendationId = 0L;  // 또는 null
        this.carId = car.getCarId();
        this.modelName = car.getModelName();
        this.fuelType = car.getFuelType();
        this.fuelEfficiency = car.getFuelEfficiency();
        this.capacity = car.getCapacity();
        this.luggageSize = car.getLuggageSize();
        this.totalPrice = car.getDailyPrice().intValue();  // BigDecimal → int
        this.basePrice = car.getDailyPrice().intValue();
        this.agencyName = car.getAgency().getAgencyName();
        this.naturalLanguageMessage = message;
    }

    public static RecommendationResponse systemMessage(String message) {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendationId(0L); // 혹은 null
        response.setCarId(0L);            // 혹은 null
        response.setModelName(message);
        response.setFuelType("");
        response.setFuelEfficiency(0.0);
        response.setCapacity(0);
        response.setLuggageSize("");
        response.setTotalPrice(0);
        response.setBasePrice(0);
        response.setAgencyName("");
        response.setNaturalLanguageMessage(message);
        return response;
    }
}
