package com.syu.cara.policy.controller;

import com.syu.cara.policy.dto.InsuranceOptionResponse;
import com.syu.cara.policy.service.InsuranceOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/insurance-options")
@RequiredArgsConstructor
public class InsuranceOptionController {

    private final InsuranceOptionService insuranceOptionService;

    @GetMapping
    public ResponseEntity<List<InsuranceOptionResponse>> getAllInsuranceOptions() {
        List<InsuranceOptionResponse> insuranceOptions = insuranceOptionService.getAllInsuranceOptions();
        return ResponseEntity.ok(insuranceOptions);
    }
}
