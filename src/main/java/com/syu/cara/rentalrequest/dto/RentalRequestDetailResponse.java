package com.syu.cara.rentalrequest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalRequestDetailResponse {
    private Long requestId;
    private Long userId;
    private String pickupLocation;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String purpose;
    private int passengerCount;
    private String luggageSize;
    private Double fuelEfficiencyPreference;
    private int budget;
    private String additionalOptions;
    private LocalDateTime createdAt;
}