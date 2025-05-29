package com.syu.cara.recommandation;

import com.syu.cara.car.Car;
import com.syu.cara.rentalrequest.RentalRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rental_request_id")
    private RentalRequest rentalRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id")
    private Car car;

    private Double score;
    private LocalDateTime recommendedAt;
}
