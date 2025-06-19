package com.syu.cara.reservation.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationDetailResponse {
    private Long reservationId;
    private Long recommendationId;
    private Long carId;
    private Long insuranceOptionId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String reservedPeriod;
    private Integer totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
