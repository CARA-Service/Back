package com.syu.cara.user.controller;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoAuthResponse;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.KakaoClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class KakaoOAuthController {
    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public KakaoOAuthController(KakaoClient kakaoClient,
                                UserRepository userRepository,
                                JwtService jwtService) {
        this.kakaoClient    = kakaoClient;
        this.userRepository = userRepository;
        this.jwtService     = jwtService;
    }

    @PostMapping("/kakao")
    public ResponseEntity<KakaoAuthResponse> kakaoLogin(@RequestBody KakaoAuthRequest request) {
        KakaoUserInfoDTO kakaoInfo;
        try {
            kakaoInfo = kakaoClient.getKakaoUserInfo(request.getAccessToken());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String kakaoId = kakaoInfo.getId();
        String loginId = "kakao_" + kakaoId;
        boolean isNewUser;

        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);
        User userEntity;

        if (optionalUser.isEmpty()) {
            isNewUser = true;
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .loginId(loginId)
                    .email(kakaoInfo.getKakao_account().getEmail())
                    .fullName(kakaoInfo.getProperties().getNickname())
                    .profileImageUrl(kakaoInfo.getProperties().getProfile_image())
                    .build();
            userEntity = userRepository.save(newUser);
        } else {
            isNewUser  = false;
            userEntity = optionalUser.get();
        }

        String jwtToken = jwtService.generateToken(userEntity);

        KakaoAuthResponse responseDto = KakaoAuthResponse.builder()
                .userId(userEntity.getUserId())
                .loginId(userEntity.getLoginId())
                .isNewUser(isNewUser)
                .jwtToken(jwtToken)
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
