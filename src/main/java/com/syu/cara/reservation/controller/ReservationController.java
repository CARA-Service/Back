package com.syu.cara.reservation.controller;

import com.syu.cara.reservation.dto.ReservationCreateRequest;
import com.syu.cara.reservation.dto.ReservationCreateResponse;
import com.syu.cara.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationCreateResponse> createReservation(@RequestBody ReservationCreateRequest request) {
        Long reservationId = reservationService.createReservation(request);
        ReservationCreateResponse response = new ReservationCreateResponse(reservationId, "결제가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}