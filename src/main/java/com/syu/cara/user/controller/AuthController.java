package com.syu.cara.user.controller;

import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoAuthResponse;
import com.syu.cara.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/kakao")
    public ResponseEntity<KakaoAuthResponse> kakaoLogin(@RequestBody KakaoAuthRequest request) {
        try {
            KakaoAuthResponse response = userService.loginWithKakao(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            // 예: Invalid access token
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "카카오 인증 실패",
                ex
            );
        }
    }
}
