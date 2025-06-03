package com.syu.cara.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 카카오 로그인 요청용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthRequest {
    private String accessToken;
}
