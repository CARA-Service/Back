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
                        - 인원: %d명
                        - 수하물 크기: %s
                        - 연비 선호도: %.1f km/l
                        - 예산: %d만원
                        - 목적: %s
                        - 추가 옵션: %s
                        
                        조건:
                        - 차량은 반드시 '픽업 위치(%s)'가 포함된 지역의 렌터카 지점(agencyName)에서만 추천할 것.
                        - 예: 픽업 위치가 '제주공항'이라면, '제주공항점', '제주시점', '제주도지점' 등만 포함 가능.
                        - '강남', '서울', '부산' 등의 다른 지역 렌터카 지점은 절대 포함하지 마라.
                        
                        위 조건에 가장 적합한 차량 5대를 추천해.
                        JSON 형식으로만 출력해. 설명은 생략하고 JSON만 응답해.
                        [
                          {
                            "recommendationId" : 1,
                            "carId": 7,
                            "modelName": "Kia K5",
                            "fuelEfficiency": 14.8,
                            "fuelType": "가솔린",
                            "capacity": 5,
                            "luggageSize": "중형",
                            "totalPrice": 178000,
                            "basePrice": 160000,
                            "agencyName": "롯데렌터카 제주공항점"
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
                request.getAdditionalOptions(),
                request.getPickupLocation() // 지역 필터 강조
        );
    }
}
