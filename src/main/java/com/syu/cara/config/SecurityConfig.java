package com.syu.cara.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 테스트용이거나 간단히 모든 요청을 허용하고 싶을 때 Security를 비활성화하는 설정.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())       // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()       // 모든 요청을 인증 없이 허용
            );
        return http.build();
    }
}
