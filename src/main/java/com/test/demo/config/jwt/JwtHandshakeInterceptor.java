package com.test.demo.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("beforeHandshake 접근됨");

        List<String> authHeaders = request.getHeaders().get("Authorization");
        log.info("Authorization 헤더: {}", authHeaders);

        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            log.info("Authorization 헤더 값: {}", authHeader);

            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("추출된 토큰: {}", token);

                try {
                    if (jwtTokenProvider.validate_token(token)) {
                        String email = jwtTokenProvider.getEmailFromToken(token);
                        log.info("토큰에서 추출한 이메일: {}", email);
                        attributes.put("email", email);
                        return true;
                    } else {
                        log.warn("토큰 검증 실패");
                    }
                } catch (ExpiredJwtException e) {
                    log.error("토큰 만료: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("토큰 검증 중 예외 발생: {}", e.getMessage());
                }
            } else {
                log.warn("Bearer 토큰 형식이 아님");
            }
        } else {
            log.warn("Authorization 헤더가 없음");
        }

        log.info("JWT 검증 실패");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 이후 처리 필요 시 구현
    }


}