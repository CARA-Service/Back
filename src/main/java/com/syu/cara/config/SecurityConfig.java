package com.syu.cara.config;

import com.syu.cara.user.security.JwtAuthenticationFilter;
import com.syu.cara.user.security.JwtService;
import com.syu.cara.user.service.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true) // 베포시 havingValue false로 바꿀것
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
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
              // 이 엔드포인트들은 Controller 레벨에서 직접 401/204 등을 처리하게 열어둡니다
              .requestMatchers(
                  "/api/v1/auth/kakao",
                  "/api/v1/auth/logout",
                  "/oauth/kakao/callback",
                  "/api/v1/users/me"
              ).permitAll()
              // 나머지는 JWT 인증 필요
              .anyRequest().authenticated()
          )
          .addFilterBefore(
              new JwtAuthenticationFilter(jwtService, userDetailsService),
              UsernamePasswordAuthenticationFilter.class
          );

        return http.build();
    }
}
