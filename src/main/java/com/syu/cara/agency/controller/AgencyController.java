package com.syu.cara.agency.controller;

import com.syu.cara.agency.domain.Agency;
import com.syu.cara.agency.service.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @GetMapping
    public ResponseEntity<List<Agency>> getAllAgencies() {
        List<Agency> agencies = agencyService.getAllAgencies();
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/by-location")
    public ResponseEntity<List<Agency>> getAgenciesByLocation(@RequestParam String location) {
        List<Agency> agencies = agencyService.getAgenciesByLocation(location);
        return ResponseEntity.ok(agencies);
    }
}
