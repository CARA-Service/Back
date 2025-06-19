package com.syu.cara.recommendation.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RentalCondition {
    private String pickupLocation;
    private String rentalDate;
    private String returnDate;
    private Integer passengerCount;
    private String luggageSize;
    private Double fuelEfficiencyPreference;
    private Integer budget;
    private String additionalOptions;
    private String purpose;

}


