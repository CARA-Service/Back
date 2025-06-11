package com.syu.cara.user.controller;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.UserProfileResponse;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final UserService userService;

    public UserController(JwtService jwtService,
                          UserRepository userRepository,
                          UserService userService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 현재 사용자의 프로필 정보를 조회합니다.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {

        // 1) 헤더 검증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);

        // 2) 토큰 검증
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3) 사용자 조회
        Long userId = jwtService.getUserIdFromToken(token);
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User u = opt.get();

        // 4) 응답 DTO 매핑
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

    /**
     * 현재 사용자의 계정을 삭제(회원탈퇴)합니다.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        // 헤더에서 토큰만 추출
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);

        // 서비스에 탈퇴 로직 위임
        userService.deleteAccount(token);

        return ResponseEntity.noContent().build();
    }
}
