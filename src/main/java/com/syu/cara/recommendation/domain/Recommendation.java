// Recommendation Entity
package com.syu.cara.recommendation.domain;

import com.syu.cara.agency.domain.Agency;
import com.syu.cara.car.domain.Car;
import com.syu.cara.rentalrequest.domain.RentalRequest;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendationId;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private RentalRequest rentalRequest;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    private BigDecimal totalPrice;
    private BigDecimal basePrice;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}