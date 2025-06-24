package com.syu.cara.user.service;

import com.syu.cara.user.dto.KakaoTokenResponse;
import com.syu.cara.user.dto.KakaoUserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoClient {

    // RestTemplate을 빈으로 등록해 두었거나, 아래처럼 직접 new RestTemplate() 해도 무방합니다.
    private final RestTemplate restTemplate = new RestTemplate();

    @Value(value = "${spring.kakao.client.client-id}")
    private String clientId;

    @Value("${spring.kakao.client.client-secret:}")
    private String clientSecret;

    @Value("${spring.kakao.client.redirect-uri}")
    private String redirectUri;

    // 1) 인가 코드 → 액세스 토큰 요청
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        System.out.println("🔍 카카오 토큰 요청 시작");
        System.out.println("📋 요청 정보:");
        System.out.println("  - Client ID: " + clientId);
        System.out.println("  - Redirect URI: " + redirectUri);
        System.out.println("  - Code: " + code.substring(0, Math.min(code.length(), 20)) + "...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        if (!clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, requestEntity, KakaoTokenResponse.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                System.err.println("❌ 카카오 토큰 발급 실패 - 응답 상태: " + response.getStatusCode());
                throw new RuntimeException("카카오 토큰 발급 실패: " + response.getStatusCode());
            }

            System.out.println("✅ 카카오 토큰 발급 성공");
            return response.getBody().getAccessToken();

        } catch (Exception e) {
            System.err.println("❌ 카카오 토큰 요청 중 예외 발생: " + e.getMessage());
            throw new RuntimeException("카카오에서 액세스 토큰 발급 실패: " + e.getMessage(), e);
        }
    }

    // 2) 액세스 토큰 → 유저 정보 조회
    public KakaoUserInfoDTO getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        System.out.println("🔍 카카오 사용자 정보 요청 시작");
        System.out.println("📋 요청 정보:");
        System.out.println("  - URL: " + userInfoUrl);
        System.out.println("  - Access Token: " + accessToken.substring(0, Math.min(accessToken.length(), 20)) + "...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoDTO> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, requestEntity, KakaoUserInfoDTO.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                System.err.println("❌ 카카오 사용자 정보 조회 실패 - 응답 상태: " + response.getStatusCode());
                throw new RuntimeException("카카오 유저 정보 조회 실패: " + response.getStatusCode());
            }

            System.out.println("✅ 카카오 사용자 정보 조회 성공");
            KakaoUserInfoDTO userInfo = response.getBody();

            // 응답 데이터 상세 로깅
            System.out.println("📋 카카오 사용자 정보:");
            System.out.println("  - ID: " + userInfo.getId());
            System.out.println("  - kakao_account: " + (userInfo.getKakao_account() != null ? "존재" : "null"));
            if (userInfo.getKakao_account() != null) {
                System.out.println("  - email: " + userInfo.getKakao_account().getEmail());
            }
            System.out.println("  - properties: " + (userInfo.getProperties() != null ? "존재" : "null"));
            if (userInfo.getProperties() != null) {
                System.out.println("  - nickname: " + userInfo.getProperties().getNickname());
            }

            return userInfo;

        } catch (Exception e) {
            System.err.println("❌ 카카오 사용자 정보 요청 중 예외 발생: " + e.getMessage());
            if (e.getMessage().contains("ip mismatched")) {
                throw new RuntimeException("카카오 IP 주소 불일치 오류. 카카오 개발자 콘솔에서 현재 서버 IP(115.91.25.180)를 플랫폼에 등록해주세요.", e);
            }
            throw new RuntimeException("카카오 유저 정보 조회 실패: " + e.getMessage(), e);
        }
    }
}
