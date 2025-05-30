// SurchargePolicy Entity
package com.syu.cara.policy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "surcharge_policy")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SurchargePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surchargePolicyId;

    private String surchargeType;
    private BigDecimal surchargeAmount;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}