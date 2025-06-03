package com.syu.cara.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoAuthResponse {
    private Long userId;
    private String loginId;
    private boolean isNewUser;
    private String jwtToken;
}
