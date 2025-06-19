package com.syu.cara.car.domain;

import com.syu.cara.agency.domain.Agency;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    private String modelName;
    private String manufacturer;
    private String category;
    
    @Column(nullable = false)
    private int capacity;
    
    private String luggageSize;
    private String fuelType;
    
    @Column(nullable = false)
    private double fuelEfficiency;
    
    private Integer dailyPrice;
    private String imageUrl;
    private String additionalOptions;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    private LocalDateTime createdAt;
}
