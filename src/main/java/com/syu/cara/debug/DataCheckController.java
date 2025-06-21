package com.syu.cara.debug;

import com.syu.cara.agency.repository.AgencyRepository;
import com.syu.cara.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DataCheckController {

    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private AgencyRepository agencyRepository;

    @GetMapping("/data-summary")
    public Map<String, Object> getDataSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // 전체 통계
        long totalCars = carRepository.count();
        long totalAgencies = agencyRepository.count();
        
        summary.put("totalCars", totalCars);
        summary.put("totalAgencies", totalAgencies);
        
        // 지역별 지점 수
        Map<String, Long> agenciesByLocation = new HashMap<>();
        agencyRepository.findAll().forEach(agency -> {
            String location = extractLocationFromAgencyName(agency.getAgencyName());
            agenciesByLocation.put(location, agenciesByLocation.getOrDefault(location, 0L) + 1);
        });
        summary.put("agenciesByLocation", agenciesByLocation);
        
        // 지역별 차량 수
        Map<String, Long> carsByLocation = new HashMap<>();
        carRepository.findAll().forEach(car -> {
            String location = extractLocationFromAgencyName(car.getAgency().getAgencyName());
            carsByLocation.put(location, carsByLocation.getOrDefault(location, 0L) + 1);
        });
        summary.put("carsByLocation", carsByLocation);
        
        return summary;
    }
    
    private String extractLocationFromAgencyName(String agencyName) {
        if (agencyName == null) return "기타";
        
        if (agencyName.contains("서울")) return "서울";
        if (agencyName.contains("부산")) return "부산";
        if (agencyName.contains("대구")) return "대구";
        if (agencyName.contains("인천")) return "인천";
        if (agencyName.contains("광주")) return "광주";
        if (agencyName.contains("대전")) return "대전";
        if (agencyName.contains("울산")) return "울산";
        if (agencyName.contains("세종")) return "세종";
        if (agencyName.contains("경기")) return "경기";
        if (agencyName.contains("강원")) return "강원";
        if (agencyName.contains("충북")) return "충북";
        if (agencyName.contains("충남")) return "충남";
        if (agencyName.contains("전북")) return "전북";
        if (agencyName.contains("전남")) return "전남";
        if (agencyName.contains("경북")) return "경북";
        if (agencyName.contains("경남")) return "경남";
        if (agencyName.contains("제주")) return "제주";
        
        return "기타";
    }
}
