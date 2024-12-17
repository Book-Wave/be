package com.test.demo.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${spring.jwt.secret}") String jwtSecret;
    private final long accessTokenExpirationInMs = 60 * 60 * 1000L;;  // 토큰 유효기간 1시간
    private final long refreshTokenExpirationInMs = 30 * 24 * 60 * 60 * 1000L; // 리프레시 토큰 유효기간 30일

    public String create_token(String email, Long member_id, String nickname) {
        return Jwts.builder()
                .setSubject(email)
                .claim("member_id", member_id)
                .claim("nickname", nickname)
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
            log.info("token expired" + e.getMessage());
            throw e;
        } catch (Exception e) {
            // 잘못된 토큰
            log.info("token error" + e.getMessage());
            throw e;
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

    public String getNicknameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("nickname", String.class);
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
        cookie.setSecure(false);   // HTTPS 연결에서만 쿠키가 전송됨
        cookie.setPath("/");      // 쿠키가 모든 경로에서 사용될 수 있도록 설정
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 동안 유효
        response.addCookie(cookie);
    }

    public void deleteRefreshTokenInCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null); // 값 제거
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 환경이면 true로 설정
        cookie.setPath("/");    // 동일한 경로로 설정
        cookie.setMaxAge(0);    // 만료 시간 0으로 설정
        response.addCookie(cookie);
    }


    public Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

}
