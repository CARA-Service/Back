package com.syu.cara.rentalrequest.dto;

import java.time.LocalDate;

public class RentalRequestCreateRequest {
    private String pickupLocation;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String purpose;
    private int passengerCount;
    private String luggageSize;
    private Double fuelEfficiencyPreference;
    private int budget;
    private String additionalOptions;
}
