package com.syu.cara.user.controller;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.KakaoAuthResponse;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import com.syu.cara.user.repository.UserRepository;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.KakaoClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth/kakao")
public class KakaoOAuthCallbackController {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public KakaoOAuthCallbackController(KakaoClient kakaoClient,
                                       UserRepository userRepository,
                                       JwtService jwtService) {
        this.kakaoClient    = kakaoClient;
        this.userRepository = userRepository;
        this.jwtService     = jwtService;
    }

    /**
     * (1) 카카오 로그인 버튼 클릭 → 카카오 인증 화면 → 사용자 동의 후
     * 카카오가 이 콜백으로 인가 코드를 전송해 준다.
     *
     * GET /api/v1/auth/kakao/callback?code={인가코드값}&state={원래 state}
     */
    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code,
                                           @RequestParam(value = "state", required = false) String state) {
        System.out.println("🔄 카카오 콜백 요청 받음");
        System.out.println("📋 콜백 정보:");
        System.out.println("  - Code: " + code.substring(0, Math.min(code.length(), 20)) + "...");
        System.out.println("  - State: " + state);

        // 1) 인가 코드로 액세스 토큰 발급 요청
        String accessToken;
        try {
            accessToken = kakaoClient.getAccessToken(code);
        } catch (RuntimeException ex) {
            // 카카오 인증 실패 혹은 잘못된 인가 코드 Exception
            System.err.println("❌ 카카오 토큰 발급 실패: " + ex.getMessage());
            return ResponseEntity.badRequest()
                    .body("카카오에서 액세스 토큰 발급 실패: " + ex.getMessage());
        }

        // 2) 액세스 토큰으로 카카오 사용자 정보 조회
        KakaoUserInfoDTO kakaoInfo = kakaoClient.getKakaoUserInfo(accessToken);
        String kakaoId = kakaoInfo.getId();
        String loginId = "kakao_" + kakaoId;

        // 카카오 계정 정보 null 체크
        if (kakaoInfo.getKakao_account() == null) {
            System.err.println("❌ kakao_account가 null입니다. 카카오 개발자 콘솔에서 동의항목을 확인하세요.");
            return ResponseEntity.badRequest()
                    .body("카카오 계정 정보에 접근할 수 없습니다. 이메일 제공에 동의해주세요.");
        }

        // 이메일 정보 추출 및 검증
//        String email = kakaoInfo.getKakao_account().getEmail();
//        if (email == null || email.trim().isEmpty()) {
//            System.err.println("❌ 이메일 정보가 없습니다. 사용자가 이메일 제공에 동의하지 않았을 수 있습니다.");
//            return ResponseEntity.badRequest()
//                    .body("이메일 정보가 필요합니다. 카카오 로그인 시 이메일 제공에 동의해주세요.");
//        }

        // 닉네임 정보 추출 (null 체크)
        String nickname = "카카오사용자"; // 기본값
        String profileImage = null;
        if (kakaoInfo.getProperties() != null) {
            if (kakaoInfo.getProperties().getNickname() != null) {
                nickname = kakaoInfo.getProperties().getNickname();
            }
            profileImage = kakaoInfo.getProperties().getProfile_image();
        }

        // 3) DB 에 기존 사용자 여부 확인 → 신규이면 저장
        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);
        User userEntity;
        boolean isNewUser;
        if (optionalUser.isEmpty()) {
            isNewUser = true;
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .loginId(loginId)
//                    .email(email)
                    .fullName(nickname)
                    .profileImageUrl(profileImage)
                    .build();
            userEntity = userRepository.save(newUser);
        } else {
            isNewUser = false;
            userEntity = optionalUser.get();
        }

        // 4) JWT 발급 (User 객체를 넘겨서 내부에서 userId, loginId 등을 claim 추가)
        String jwtToken = jwtService.generateToken(userEntity);

        // 5) 클라이언트(프론트) 쪽으로 어떻게 전달할지는 상황에 따라 선택
        //    아래 예시는 “간단하게 JSON으로 반환”하는 방식입니다.
        // 프론트엔드로 리다이렉트 (JWT 토큰을 쿼리 파라미터로 전달)
        String frontendUrl = "http://localhost:5173/auth/kakao/callback";
        String redirectUrl = String.format("%s?token=%s&isNewUser=%s",
                frontendUrl, jwtToken, isNewUser);

        System.out.println("✅ 카카오 로그인 성공 - 프론트엔드로 리다이렉트: " + redirectUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }
}
