package com.syu.cara.reservation.domain;

import com.syu.cara.user.domain.User;
import com.syu.cara.car.domain.CarAvailability;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "availability_id", nullable = false)
    private CarAvailability carAvailability;

    private LocalDate rentalDate;
    private LocalDate returnDate;

    @Column(nullable = false)
    private String status;  // 예: "CONFIRMED", "CANCELLED"
}
