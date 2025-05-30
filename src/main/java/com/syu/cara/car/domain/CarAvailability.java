// CarAvailability Entity
package com.syu.cara.car.domain;

import com.syu.cara.agency.domain.Agency;
import com.syu.cara.policy.domain.DiscountPolicy;
import com.syu.cara.policy.domain.InsuranceOption;
import com.syu.cara.policy.domain.SurchargePolicy;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_availability")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CarAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityId;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private LocalDate rentalDate;
    private BigDecimal dailyPrice;

    @ManyToOne
    @JoinColumn(name = "discount_policy_id")
    private DiscountPolicy discountPolicy;

    @ManyToOne
    @JoinColumn(name = "surcharge_policy_id")
    private SurchargePolicy surchargePolicy;

    @ManyToOne
    @JoinColumn(name = "insurance_option_id")
    private InsuranceOption insuranceOption;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}