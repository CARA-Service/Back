package com.syu.cara.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.syu.cara.car.domain.Car;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationResponse {
    private Car car;
    private String gptMessage;
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

    public RecommendationResponse(Car car, String gptMessage) {
        this.car = car;
        this.gptMessage = gptMessage;
        this.carId = car.getCarId();
        this.modelName = car.getModelName();
        this.fuelType = car.getFuelType();
        this.fuelEfficiency = car.getFuelEfficiency();
        this.capacity = car.getCapacity();
        this.luggageSize = car.getLuggageSize();
        this.totalPrice = car.getDailyPrice();  // 또는 할인 적용된 가격
        this.basePrice = car.getDailyPrice();   // 할인 전 기본 가격
        this.agencyName = (car.getAgency() != null) ? car.getAgency().getAgencyName() : null;
    }

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
