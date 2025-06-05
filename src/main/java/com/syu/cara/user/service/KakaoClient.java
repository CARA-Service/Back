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

    @Value("${spring.kakao.client.client-id}")
    private String clientId;

    @Value("${spring.kakao.client.client-secret:}")
    private String clientSecret;

    @Value("${spring.kakao.client.redirect-uri}")
    private String redirectUri;

    // 1) 인가 코드 → 액세스 토큰 요청
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

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

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
            tokenUrl, HttpMethod.POST, requestEntity, KakaoTokenResponse.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("카카오 토큰 발급 실패");
        }
        // ↓ 여기서 호출하는 메서드는 getAccessToken() 이어야 합니다.
        return response.getBody().getAccessToken();
    }

    // 2) 액세스 토큰 → 유저 정보 조회
    public KakaoUserInfoDTO getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoDTO> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, requestEntity, KakaoUserInfoDTO.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("카카오 유저 정보 조회 실패");
        }
        return response.getBody();
    }
}
