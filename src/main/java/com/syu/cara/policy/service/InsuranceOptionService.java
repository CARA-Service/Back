package com.syu.cara.policy.service;

import com.syu.cara.policy.dto.InsuranceOptionResponse;

import java.util.List;

public interface InsuranceOptionService {
    List<InsuranceOptionResponse> getAllInsuranceOptions();
}
