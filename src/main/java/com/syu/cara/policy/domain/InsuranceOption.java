// InsuranceOption Entity
package com.syu.cara.policy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_option")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class InsuranceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceOptionId;

    private String insuranceType;
    private BigDecimal insuranceFee;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}