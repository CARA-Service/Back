package com.syu.cara.user.controller;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.LoginRequest;
import com.syu.cara.user.dto.SignupRequest;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.CustomUserDetails;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        if (userRepository.existsByLoginId(req.getLoginId())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("이미 사용 중인 loginId 입니다.");
        }

        User user = User.builder()
            .loginId(req.getLoginId())
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .fullName(req.getName())
            .email(req.getEmail())
            .phoneNumber(req.getPhoneNumber())
            .birthDate(req.getBirthDate())
            .address(req.getAddress())
            .build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

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
