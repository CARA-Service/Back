package com.syu.cara.reservation.dto;

import java.time.LocalDate;

public class ReservationDetailResponse {
    private Long reservationId;
    private Long userId;
    private Long availabilityId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private String status;
}
