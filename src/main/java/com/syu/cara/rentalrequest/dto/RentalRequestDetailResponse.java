package com.syu.cara.rentalrequest.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RentalRequestDetailResponse {
    private Long requestId;
    private Long userId;
    private String pickupLocation;
    private double pLatitude;
    private double pLongitude;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String purpose;
    private Integer passengerCount;
    private String luggageSize;
    private BigDecimal fuelEfficiencyPreference;
    private Integer budget;
    private String additionalOptions;
    private LocalDateTime createdAt;
}