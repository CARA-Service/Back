package com.syu.cara.user.service;

import com.syu.cara.user.dto.KakaoUserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final RestTemplate restTemplate;

    // 만약 application.yml에 Kakao API 기본 URL이나 key를 넣어두었다면 @Value로 주입 가능
    @Value("${kakao.userinfo.url:https://kapi.kakao.com/v2/user/me}")
    private String kakaoUserInfoUrl;

    /**
     * 카카오 서버로부터 사용자 정보를 가져오는 메서드.
     * @param accessToken 카카오에서 발급받은 액세스 토큰
     * @return KakaoUserInfo - 매핑된 사용자 정보 DTO
     * @throws IllegalArgumentException 잘못된 응답일 경우 예외
     */
    public KakaoUserInfoDTO getKakaoUserInfo(String accessToken) {
        // 1) HTTP 헤더에 Bearer 토큰 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // 2) RestTemplate을 이용해 GET 요청
        ResponseEntity<KakaoUserInfoDTO> response = restTemplate.exchange(
                kakaoUserInfoUrl,
                HttpMethod.GET,
                entity,
                KakaoUserInfoDTO.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalArgumentException("카카오 사용자 정보 조회 실패");
        }

        return response.getBody();
    }
}
