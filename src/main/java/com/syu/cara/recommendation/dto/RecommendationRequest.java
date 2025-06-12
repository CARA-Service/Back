package com.syu.cara.recommendation.dto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RecommendationRequest {
    private Long userId;

    private String pickupLocation;

    private LocalDate rentalDate;
    private LocalDate returnDate;

    private int passengerCount;
    private String luggageSize;
    private String purpose;

    private double fuelEfficiencyPreference;
    private int budget;

    private String additionalOptions;
}
