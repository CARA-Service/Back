package com.syu.cara.user.service;

import com.syu.cara.user.domain.User;
import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoAuthResponse;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import com.syu.cara.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KakaoClient kakaoClient;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public KakaoAuthResponse loginWithKakao(KakaoAuthRequest request) {
        // 1. 카카오 API로 사용자 정보 조회
        KakaoUserInfoDTO kakaoInfo = kakaoClient.getKakaoUserInfo(request.getAccessToken());
        String kakaoId = kakaoInfo.getId();
        String email = kakaoInfo.getKakao_account() != null
                ? kakaoInfo.getKakao_account().getEmail()
                : null;
        String nickname = kakaoInfo.getProperties() != null
                ? kakaoInfo.getProperties().getNickname()
                : null;
        String profileImageUrl = kakaoInfo.getProperties() != null
                ? kakaoInfo.getProperties().getProfile_image()
                : null;

        // 2. DB: 기존 카카오 ID로 가입된 사용자가 있는지 확인
        User user = userRepository.findByKakaoId(kakaoId)
            .orElseGet(() -> {
             // 신규 회원 생성할 때 passwordHash를 null이 아닌 값(랜덤 문자열 해시)으로 설정
                String tempPassword = UUID.randomUUID().toString();
                User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .loginId("kakao_" + kakaoId)
                    .fullName(kakaoInfo.getProperties().getNickname())
                    .email(kakaoInfo.getKakao_account().getEmail())
                    .profileImageUrl(kakaoInfo.getProperties().getProfile_image())
                    .passwordHash(passwordEncoder.encode(tempPassword))
                    .createdAt(LocalDateTime.now())
                    .build();
                return userRepository.save(newUser);
            });

        boolean isNew = (user.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(10)));
        // 위 판별은 아주 대략적입니다. 실제 구현에선 save 직후 반환된 엔티티가
        // 새로 생성된 건인지 구분하는 로직을 따로 두는 편이 좋습니다.

        // 3. JWT 토큰 발급 (선택) - 예시: generateJwtToken(user)
        String jwtToken = generateJwtToken(user);

        // 4. 응답 DTO 생성
        return KakaoAuthResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .isNewUser(isNew)
                .jwtToken(jwtToken)
                .build();
    }

    private String generateJwtToken(User user) {
        // 실제로는 Spring Security + JWT 라이브러리를 이용해서 토큰 생성.
        // 예시로 “fake-jwt-{userId}” 형태의 문자열 리턴:
        return "fake-jwt-" + user.getUserId();
    }
}
