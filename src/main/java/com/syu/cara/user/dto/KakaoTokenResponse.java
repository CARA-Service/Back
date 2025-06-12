package com.syu.cara.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;                   // 액세스 토큰 만료 시간(초)

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;       // 리프레시 토큰 만료 시간(초)

    private String scope;                     // (예: "account_email profile")
}
