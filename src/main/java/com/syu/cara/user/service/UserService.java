package com.syu.cara.user.service;

import com.syu.cara.user.dto.KakaoAuthRequest;
import com.syu.cara.user.dto.KakaoAuthResponse;

public interface UserService {

    /**
     * 카카오 소셜 로그인 또는 회원가입
     * @param request 카카오 엑세스 토큰
     * @return 카카오 로그인 결과 (userId, loginId, isNewUser, jwtToken)
     */
    KakaoAuthResponse loginWithKakao(KakaoAuthRequest request);
}