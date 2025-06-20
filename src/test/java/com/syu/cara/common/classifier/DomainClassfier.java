package com.syu.cara.common.classifier;
import com.syu.cara.chat.dto.ChatRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainClassifierTest {

    private DomainClassifier domainClassifier;

    @BeforeEach
    void setUp() {
        domainClassifier = new DomainClassifier(); // @Component 없어도 수동 생성 가능
    }

    @Test
    void testRentalDomain_trueCases() {
        assertTrue(domainClassifier.isRentalDomain("렌터카 예약하고 싶어요"));
        assertTrue(domainClassifier.isRentalDomain("차량 대여 가능할까요?"));
        assertTrue(domainClassifier.isRentalDomain("픽업 시간 알려줘"));
        assertTrue(domainClassifier.isRentalDomain("수하물 크기에 맞는 차 추천해줘"));
    }

    @Test
    void testRentalDomain_falseCases() {
        assertFalse(domainClassifier.isRentalDomain("오늘 날씨 어때?"));
        assertFalse(domainClassifier.isRentalDomain("호텔 추천해줘"));
        assertFalse(domainClassifier.isRentalDomain("점심 뭐 먹을까?"));
        assertFalse(domainClassifier.isRentalDomain(""));
        assertFalse(domainClassifier.isRentalDomain(null));
    }

    @Test
    void testChatRequestRentalDomain() {
        ChatRequest request = new ChatRequest();
        request.setMessage("렌터카 픽업 장소 알려줘");

        assertTrue(domainClassifier.isRentalDomain(request.getMessage()));
    }

    @Test
    void testChatRequestNonRentalDomain() {
        ChatRequest request = new ChatRequest();
        request.setMessage("요즘 날씨가 어때?");

        assertFalse(domainClassifier.isRentalDomain(request.getMessage()));
    }

}
