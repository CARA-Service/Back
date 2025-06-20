package com.syu.cara.reservation.service;

import com.syu.cara.reservation.dto.ReservationCreateRequest;

public interface ReservationService {
    Long createReservation(ReservationCreateRequest request);
}