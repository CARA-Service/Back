package com.syu.cara.user.dto;

import lombok.Data;

/**
 * 카카오 API로부터 받은 JSON 응답을 매핑할 DTO.
 */
@Data
public class KakaoUserInfoDTO {
    private String id;           // 카카오가 주는 고유 ID (예: "1234567890")
    private KakaoAccount kakao_account;
    private Properties properties;

    @Data
    public static class KakaoAccount {
        private String email;     // 이메일 (동의 시)
    }

    @Data
    public static class Properties {
        private String nickname;          // 프로필 닉네임
        private String profile_image;     // 프로필 이미지 URL
    }
}
