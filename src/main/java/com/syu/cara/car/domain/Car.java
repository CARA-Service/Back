// Car Entity
package com.syu.cara.car.domain;

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
    private Long carId;

    private String modelName;
    private String manufacturer;
    private String category;
    private int capacity;
    private String luggageSize;
    private String fuelType;
    private double fuelEfficiency;
    private String imageUrl;
    private String additionalOptions;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
