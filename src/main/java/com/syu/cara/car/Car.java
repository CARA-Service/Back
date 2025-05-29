package com.syu.cara.car;

import com.syu.cara.agency.Agency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;

    @Enumerated(EnumType.STRING)
    private Category category;         // COMPACT, SEDAN, SUV, VAN

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;         // GASOLINE, DIESEL, ELECTRIC, HYBRID

    private Integer seatingCapacity;
    private BigDecimal dailyPrice;
    private Boolean available;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    public enum Category { COMPACT, SEDAN, SUV, VAN }
    public enum FuelType  { GASOLINE, DIESEL, ELECTRIC, HYBRID }
}
