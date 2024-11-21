package com.test.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}") String jwtSecret;
    private final long accessTokenExpirationInMs = 60 * 60 * 1000L;;  // 토큰 유효기간 1시간
    private final long refreshTokenExpirationInMs = 7 * 24 * 60 * 60 * 1000L;  // 7일 (7 * 24 * 60 * 60 * 1000ms)

    public String create_token(String email, Long member_id) {
        return Jwts.builder()
                .setSubject(email)
                .claim("member_id", member_id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

//    public String create_refresh_token(String email, Long member_id) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("member_id", member_id)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMs))
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰 만료
            System.out.println("토큰 만료: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // 잘못된 토큰
            System.out.println("토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 이후의 토큰 부분만 반환
        }
        return null;
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

}
