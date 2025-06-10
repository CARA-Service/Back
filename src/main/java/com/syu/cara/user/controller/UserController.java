package com.syu.cara.user.controller;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.UserProfileResponse;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserController(JwtService jwtService,
                          UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {

        // 1) 헤더에서 Bearer 제거
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);

        // 2) 토큰 검증
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3) 토큰에서 userId 꺼내서 조회
        Long userId = jwtService.getUserIdFromToken(token);
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User u = opt.get();

        // 4) DTO로 변환
        UserProfileResponse dto = new UserProfileResponse(
            u.getLoginId(),
            u.getEmail(),
            u.getFullName(),
            u.getProfileImageUrl(),
            u.getDriverLicenseNumber(),
            u.getAddress()
        );
        return ResponseEntity.ok(dto);
    }
}
