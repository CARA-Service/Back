package com.syu.cara.common.classifier;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainClassifier {
    // 렌터카 도메인 키워드 리스트
    private static final List<String> RENTAL_KEYWORDS = List.of(
            "렌터카", "렌트카", "차량", "대여", "픽업","여행", "반납", "예약", "연비", "차", "승차", "수하물", "보험", "운전"
    );

    // 사용자의 자연어 입력이 렌터카 도메인 관련인지 판별
    public boolean isRentalDomain(String userInput) {
        if (userInput == null || userInput.isBlank()) return false;

        String lower = userInput.toLowerCase();
        return RENTAL_KEYWORDS.stream().anyMatch(lower::contains);
    }
}
