package com.syu.cara.user.security;

import com.syu.cara.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 매 요청마다 Authorization 헤더의 Bearer 토큰을 확인하고,
 * 유효한 경우 SecurityContext에 인증 정보를 설정하는 필터
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1) Authorization 헤더에서 토큰 추출 (Bearer {token})
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 2) 토큰 검증
            if (jwtService.validateToken(token)) {
                // 3) 토큰에서 꺼낸 userId 또는 loginId 정보를 통해 UserDetails 조회
                Long userId = jwtService.getUserIdFromToken(token);

                // 예시: userDetailsService 로부터 UserDetails 객체를 얻어서 Authentication 생성
                var userDetails = userDetailsService.loadUserById(userId);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
