package com.test.demo.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}") String jwtSecret;
    private final long accessTokenExpirationInMs = 1 * 60 * 1000L;;  // 토큰 유효기간 1분
    private final long refreshTokenExpirationInMs = 30 * 24 * 60 * 60 * 1000L; // 리프레시 토큰 유효기간 30일

    public String create_token(String email, Long member_id) {
        return Jwts.builder()
                .setSubject(email)
                .claim("member_id", member_id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String create_refresh_token(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validate_token(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰 만료
            System.out.println("토큰 만료: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // 잘못된 토큰
            System.out.println("토큰 검증 실패: " + e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    public boolean validate_refresh_token(String refresh_token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(refresh_token);
            return true;
        } catch (Exception e) {
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

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void setRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        cookie.setSecure(true);   // HTTPS 연결에서만 쿠키가 전송됨
        cookie.setPath("/");      // 쿠키가 모든 경로에서 사용될 수 있도록 설정
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 동안 유효
        response.addCookie(cookie);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

}
