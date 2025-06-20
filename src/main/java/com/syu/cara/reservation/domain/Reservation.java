package com.syu.cara.reservation.domain;

import com.syu.cara.policy.domain.InsuranceOption;
import com.syu.cara.recommendation.domain.Recommendation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;
    
    @ManyToOne
    @JoinColumn(name = "recommendation_id")
    private Recommendation recommendation;
    
    @ManyToOne
    @JoinColumn(name = "insurance_option_id")
    private InsuranceOption insuranceOption;

    @Column(nullable = false, name = "rental_date")
    private LocalDate rentalDate;
    
    @Column(nullable = false, name = "return_date")
    private LocalDate returnDate;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(nullable = false, name = "status")
    private String status;

    @Column(name = "payment")
    private String payment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
