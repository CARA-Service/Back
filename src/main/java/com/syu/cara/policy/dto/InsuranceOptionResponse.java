package com.syu.cara.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceOptionResponse {
    private Long insuranceOptionId;
    private String insuranceType;
    private BigDecimal insuranceFee;
}
