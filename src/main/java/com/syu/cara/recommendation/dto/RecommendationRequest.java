package com.syu.cara.recommendation.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RecommendationRequest {
    private Long userId;
    private String pickupLocation;
    private double pLatitude;
    private double pLongitude;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Integer passengerCount;
    private String luggageSize;
    private String purpose;
    private BigDecimal fuelEfficiencyPreference;
    private Integer budget;
    private String additionalOptions;
}
