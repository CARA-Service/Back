package com.syu.cara.user.security;

import com.syu.cara.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Component
@Service
public class JwtService {

    // application.yml 또는 properties에서 값을 주입받는다.
    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.expirationMillis}")
    private long expirationMillis;

    // 서명(Signature)에 사용할 Key 객체 생성 (HS256 알고리즘)
    private Key getSigningKey() {
        // Base64로 인코딩된 시크릿 키를 디코딩해서 Key 생성 (만약 yaml에 Base64 문자열로 적어두셨다면)
        // byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // return Keys.hmacShaKeyFor(keyBytes);

        // 만약 plain-text 형태로 적어두셨다면(간단히 getBytes() 해서 사용)
        byte[] keyBytes = secretKey.getBytes(); 
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 1) JWT 토큰 발급 메서드
     *    - user 객체에서 필요한 정보를 꺼내서 claim으로 넣을 수 있다.
     *    - 예시로 userId, loginId를 subject 및 claim에 넣고, 발급 시각/만료 시각을 설정했다.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(user.getLoginId())            // 토큰의 subject: 여기서는 로그인 ID
                .claim("userId", user.getUserId())        // 추가로 userId를 claim에 넣어둠
                // .claim("roles", user.getRoles())        // (필요하면 권한 정보도 넣어둔다)
                .setIssuedAt(now)                         // 발급 시간
                .setExpiration(expiry)                    // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 2) JWT 토큰에서 userId 추출 (Claim에서 가져오는 예시)
     *    토큰을 검증한 뒤, 내부 claim에 담겨있는 userId를 꺼낸다.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        // claim에서 꺼낼 때 Long 타입으로 캐스팅
        return claims.get("userId", Long.class);
    }

    /**
     * 3) 토큰 유효성 검증 메서드
     *    - 시그니처 확인, 만료 시간 확인 등을 수행
     *    - 유효하면 true, 아니면 예외를 던지거나 false
     */
    public boolean validateToken(String token) {
        try {
            // parseClaims() 내부에서 서명 및 만료 시간 검증이 이루어진다.
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            // 토큰이 만료된 경우
            System.out.println("JWT 만료: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.out.println("지원하지 않는 JWT: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("잘못된 JWT 서식: " + ex.getMessage());
        } catch (SignatureException ex) {
            System.out.println("JWT 서명 검증 실패: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT 토큰이 비었거나 잘못됨: " + ex.getMessage());
        }
        return false;
    }

    /**
     * 파싱해서 Claims를 반환하는 내부 헬퍼 메서드
     * -> validateToken(...)에서 참고하고, getUserIdFromToken(...)에서도 사용한다.
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)    // JWS 서명 검증 및 claims 파싱
                .getBody();
    }
}
