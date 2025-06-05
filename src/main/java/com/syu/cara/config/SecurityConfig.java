package com.syu.cara.config;

import com.syu.cara.user.security.JwtAuthenticationFilter;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtService jwtService,
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // “카카오 로그인” POST 엔드포인트 (프로그램 내부 테스트용)
                .requestMatchers("/api/auth/kakao").permitAll()
                // **콜백용 GET 엔드포인트도 반드시 permitAll 처리해줘야 합니다**
                .requestMatchers("/oauth/kakao/callback").permitAll()
                // 그 외는 인증 필요
                .anyRequest().authenticated()
            );

        // JWT 필터 등록 (있다면)
        JwtAuthenticationFilter jwtFilter =
            new JwtAuthenticationFilter(jwtService, customUserDetailsService);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
