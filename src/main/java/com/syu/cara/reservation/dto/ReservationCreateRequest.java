package com.syu.cara.reservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationCreateRequest {
    @JsonProperty("recommendation_id")
    private Long recommendationId;
    
    @JsonProperty("insurance_option_id")
    private Long insuranceOptionId;
    
    @JsonProperty("rental_date")
    private LocalDate rentalDate;
    
    @JsonProperty("return_date")
    private LocalDate returnDate;
    
    @JsonProperty("total_price")
    private Integer totalPrice;
    
    @JsonProperty("payment")
    private String payment;
}
