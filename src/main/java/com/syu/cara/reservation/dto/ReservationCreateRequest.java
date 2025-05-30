package com.syu.cara.reservation.dto;

import java.time.LocalDate;

public class ReservationCreateRequest {
    private Long userId;
    private Long availabilityId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
}
