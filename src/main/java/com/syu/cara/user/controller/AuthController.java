package com.syu.cara.user.controller;

import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoAuthResponse;
import com.syu.cara.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * [POST] /api/auth/kakao
     * 클라이언트로부터 받은 accessToken을 이용해 카카오 로그인/회원가입 처리
     */
    @PostMapping("/kakao")
    public ResponseEntity<KakaoAuthResponse> kakaoLogin(
            @RequestBody KakaoAuthRequest request) {

        KakaoAuthResponse response = userService.loginWithKakao(request);
        return ResponseEntity.ok(response);
    }
}
