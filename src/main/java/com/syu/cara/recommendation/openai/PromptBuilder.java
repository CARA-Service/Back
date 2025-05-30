package com.syu.cara.recommendation.openai;

import com.syu.cara.recommendation.dto.RecommendationRequest;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {
    public String buildPrompt(RecommendationRequest request) {
        return String.format("""
            고객 차량 대여 요청:
            - 픽업 위치: %s
            - 대여일: %s
            - 반납일: %s
            - 인원: %d
            - 수하물 크기: %s
            - 연비 선호도: %.1f km/l
            - 예산: %d만원
            - 목적: %s
            - 추가 옵션: %s

            위 조건에 가장 적합한 차량 3대를 추천해주세요.
            JSON 형식으로:
            [
              {
                "carId": 7,
                "modelName": "Kia K5",
                "fuelEfficiency": 14.8,
                "fuelType": "가솔린",
                "capacity": 5,
                "luggageSize": "중형",
                "totalPrice": 178000,
                "basePrice": 160000,
                "agencyName": "카카오렌터카 강남점"
              }
            ]
            """,
                request.getPickupLocation(),
                request.getRentalDate(),
                request.getReturnDate(),
                request.getPassengerCount(),
                request.getLuggageSize(),
                request.getFuelEfficiencyPreference(),
                request.getBudget(),
                request.getPurpose(),
                request.getAdditionalOptions()
        );
    }
}
