package com.syu.cara.car.domain;

import com.syu.cara.agency.domain.Agency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;  // 🔁 FK 관계

    private String modelName;
    private String manufacturer;
    private String category;

    private int capacity;
    private String luggageSize;

    private String fuelType;
    private double fuelEfficiency;

    private BigDecimal dailyPrice;

    private String imageUrl;
    private String additionalOptions;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
