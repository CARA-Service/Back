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
    private Long agencyId;

    private String agencyName;
    private String location;
    private String businessRegistrationNumber;
    private String operatingHours;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}