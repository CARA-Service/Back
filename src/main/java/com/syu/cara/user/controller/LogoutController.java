package com.syu.cara.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그아웃 처리용 컨트롤러
 * 클라이언트에 저장된 JWT 쿠키를 삭제하고 204 No Content를 반환
 */
@RestController
@RequestMapping("/api/v1/auth")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // JWT를 HttpOnly 쿠키에 저장하는 경우에만 쿠키를 삭제합니다.
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 클라이언트 로컬 스토리지나 세션 스토리지는 클라이언트 측에서 삭제
        return ResponseEntity.noContent().build();
    }
}
