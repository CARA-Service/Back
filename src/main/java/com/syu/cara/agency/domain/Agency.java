// Agency Entity
package com.syu.cara.agency.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_agency")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agency_id")
    private Long agencyId;

    @Column(nullable = false)
    private String agencyName;
    
    private String location;
    private String businessRegistrationNumber;
    private String operatingHours;
    
    @Column(nullable = false)
    private double latitude;
    
    @Column(nullable = false)
    private double longitude;

    private LocalDateTime createdAt;
}