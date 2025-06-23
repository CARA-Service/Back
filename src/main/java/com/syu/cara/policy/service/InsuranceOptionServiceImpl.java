package com.syu.cara.policy.service;

import com.syu.cara.policy.domain.InsuranceOption;
import com.syu.cara.policy.dto.InsuranceOptionResponse;
import com.syu.cara.policy.repository.InsuranceOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsuranceOptionServiceImpl implements InsuranceOptionService {

    private final InsuranceOptionRepository insuranceOptionRepository;

    @Override
    public List<InsuranceOptionResponse> getAllInsuranceOptions() {
        List<InsuranceOption> insuranceOptions = insuranceOptionRepository.findAll();
        
        return insuranceOptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private InsuranceOptionResponse convertToResponse(InsuranceOption insuranceOption) {
        return InsuranceOptionResponse.builder()
                .insuranceOptionId(insuranceOption.getInsuranceOptionId())
                .insuranceType(insuranceOption.getInsuranceType())
                .insuranceFee(insuranceOption.getInsuranceFee())
                .build();
    }
}
