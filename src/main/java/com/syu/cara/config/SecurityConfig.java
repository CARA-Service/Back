package com.syu.cara.config;

import com.syu.cara.user.security.JwtAuthenticationFilter;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtService jwtService,
                          CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // CSRF, 세션 스태틱리스
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          // 엔드포인트별 접근 제어
          .authorizeHttpRequests(auth -> auth
              // 카카오 로그인 / 콜백
              .requestMatchers("/api/v1/auth/kakao", "/oauth/kakao/callback").permitAll()
              // 로그아웃은 인증된 사용자만
              .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
              // 내 정보 조회
              .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
              // 회원 탈퇴
              .requestMatchers(HttpMethod.DELETE, "/api/v1/users").permitAll().anyRequest().authenticated()
              // 그 외 요청도 인증 필요
              .anyRequest().authenticated()
          )
          // JWT 필터 등록 (UsernamePasswordAuthenticationFilter 앞)
          .addFilterBefore(
              new JwtAuthenticationFilter(jwtService, userDetailsService),
              UsernamePasswordAuthenticationFilter.class
          );

        return http.build();
    }
}
