// ReservationRepository
package com.syu.cara.reservation.repository;

import com.syu.cara.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}