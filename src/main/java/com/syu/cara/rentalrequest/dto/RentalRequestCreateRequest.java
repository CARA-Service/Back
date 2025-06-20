package com.syu.cara.rentalrequest.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RentalRequestCreateRequest {
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
}
