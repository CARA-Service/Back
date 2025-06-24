package com.syu.cara.recommendation.controller;

import com.syu.cara.recommendation.dto.RecommendationRequest;
import com.syu.cara.recommendation.dto.RecommendationResponse;
import com.syu.cara.recommendation.service.LLMRecommendationService;
import com.syu.cara.user.domain.User;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LLMRecommendationController {

    private final LLMRecommendationService recommendationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/recommendations")
    public ResponseEntity<List<RecommendationResponse>> recommend(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RecommendationRequest request) {

        // 1. JWT 토큰에서 사용자 정보 추출
        Long userId = extractAndValidateUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. 추천 서비스 호출 (사용자 정보 포함)
        List<RecommendationResponse> result = recommendationService.generateRecommendation(request.getUserInput(), user);
        return ResponseEntity.ok(result);
    }

    private Long extractAndValidateUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return null;
        }
        return jwtService.getUserIdFromToken(token);
    }
}
