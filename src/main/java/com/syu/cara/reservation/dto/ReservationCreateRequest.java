package com.syu.cara.reservation.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationCreateRequest {
    private Long recommendationId;
    private Long carId;
    private Long insuranceOptionId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String reservedPeriod;
    private Integer totalPrice;
}
