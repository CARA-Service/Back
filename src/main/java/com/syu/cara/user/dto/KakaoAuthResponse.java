package com.syu.cara.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoAuthResponse {
    private Long userId;
    private String loginId;

    // "isNewUser"라는 이름으로 직렬화
    @JsonProperty("isNewUser")
    private boolean isNewUser;

    private String jwtToken;
}
