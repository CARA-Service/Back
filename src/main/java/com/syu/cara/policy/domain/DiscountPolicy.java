// DiscountPolicy Entity
package com.syu.cara.policy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_policy")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class DiscountPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountPolicyId;

    private String discountType;
    private BigDecimal discountAmount;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
