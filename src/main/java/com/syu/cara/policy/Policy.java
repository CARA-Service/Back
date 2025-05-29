package com.syu.cara.policy;

import com.syu.cara.car.Car;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "policies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PolicyType type;           // CANCELLATION, INSURANCE, FUEL

    private String description;
    private BigDecimal flatFee;        // 고정 요금
    private BigDecimal percentageFee;  // 비율 요금

    @ManyToMany
    @JoinTable(
      name = "car_policy",
      joinColumns = @JoinColumn(name = "policy_id"),
      inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private Set<Car> applicableCars;

    public enum PolicyType { CANCELLATION, INSURANCE, FUEL }
}
