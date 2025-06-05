package com.syu.cara.user.controller;

import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoAuthResponse;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import com.syu.cara.user.domain.User;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.service.KakaoClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    // (추가로 JwtService 같은 걸 주입하려면 여기에 필드 선언)

    public AuthController(KakaoClient kakaoClient,
                          UserRepository userRepository /*, JwtService jwtService */) {
        this.kakaoClient    = kakaoClient;
        this.userRepository = userRepository;
        // this.jwtService = jwtService;
    }

    @PostMapping("/kakao")
    public ResponseEntity<KakaoAuthResponse> kakaoLogin(@RequestBody KakaoAuthRequest request) {
        KakaoUserInfoDTO kakaoInfo;
        try {
            // 1) 카카오 서버에서 사용자 정보 가져오기 (예외 발생 가능)
            kakaoInfo = kakaoClient.getKakaoUserInfo(request.getAccessToken());
        } catch (RuntimeException ex) {
            // 잘못된 토큰 등으로 카카오 클라이언트가 RuntimeException을 던지면 401로 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2) 정상적으로 사용자 정보를 받아왔을 때 처리 흐름
        String kakaoId = kakaoInfo.getId();                     // ex: "55555"
        String loginId = "kakao_" + kakaoId;                     // ex: "kakao_55555"
        boolean isNewUser;

        // (Optional<User>로 리턴한다고 가정)
        Optional<User> optionalExisting = userRepository.findByKakaoId(kakaoId);
        User userEntity;

        if (optionalExisting.isEmpty()) {
            // → 신규 회원
            isNewUser = true;
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .loginId(loginId)
                    .email(kakaoInfo.getKakao_account().getEmail())
                    .fullName(kakaoInfo.getProperties().getNickname())
                    .profileImageUrl(kakaoInfo.getProperties().getProfile_image())
                    // 필요한 다른 필드가 있으면 추가…
                    .build();
            userEntity = userRepository.save(newUser);
        } else {
            // → 기존 회원
            isNewUser = false;
            userEntity = optionalExisting.get();
        }

        // 3) JWT 토큰 생성 (예시로 Fake 토큰)
        String jwtToken = /* jwtService.generateToken(userEntity) */ "fake-jwt-" + userEntity.getUserId();

        // 4) 응답 DTO 작성
        KakaoAuthResponse responseDto = KakaoAuthResponse.builder()
                .userId(userEntity.getUserId())
                .loginId(userEntity.getLoginId())
                .isNewUser(isNewUser)
                .jwtToken(jwtToken)
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
