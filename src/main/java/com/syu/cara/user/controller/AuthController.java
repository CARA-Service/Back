package com.syu.cara.user.controller;

import com.syu.cara.user.dto.LoginRequest;
import com.syu.cara.user.security.CustomUserDetails;
import com.syu.cara.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getLoginId(), request.getPassword()
            )
        );

        // 인증 성공 시 CustomUserDetails로부터 User 엔티티 꺼내옴
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // JWT 토큰 발급
        String token = jwtService.generateToken(userDetails.getUser());
        // 토큰 리턴
        return ResponseEntity.ok(Map.of("token", token));
    }
}
