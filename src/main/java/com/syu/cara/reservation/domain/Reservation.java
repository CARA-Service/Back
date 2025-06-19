package com.syu.cara.reservation.domain;

import com.syu.cara.car.domain.Car;
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
    @JoinColumn(name = "car_id")
    private Car car;
    
    @ManyToOne
    @JoinColumn(name = "insurance_option_id")
    private InsuranceOption insuranceOption;
    
    @Column(nullable = false)
    private LocalDate rentalDate;
    
    @Column(nullable = false)
    private LocalDate returnDate;
    
    private String reservedPeriod;
    private Integer totalPrice;
    
    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;
}
