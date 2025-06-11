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
    private final CustomUserDetailsService userDetailsService;

    // JwtService, CustomUserDetailsService는 별도 @Component/@Service 어노테이션이 붙어 있어야 스캔됩니다.
    public SecurityConfig(JwtService jwtService,
                          CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // HttpSecurity는 여기서만 사용, 생성자나 필드 주입 X
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
              // 로그인, 콜백 엔드포인트는 무조건 열어두고
              .requestMatchers("/api/v1/auth/kakao", "/oauth/kakao/callback").permitAll()
              // 사용자 정보 조회는 JWT 인증 필요
              .requestMatchers("/api/v1/users/me").authenticated()
              // logout 엔드포인트 허용
              .requestMatchers("/api/v1/auth/logout").permitAll()
              // 나머지 요청도 인증 필요
              .anyRequest().authenticated()
          )
          // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
          .addFilterBefore(
              new JwtAuthenticationFilter(jwtService, userDetailsService),
              UsernamePasswordAuthenticationFilter.class
          );

        return http.build();
    }
}
