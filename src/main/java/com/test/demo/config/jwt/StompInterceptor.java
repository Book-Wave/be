package com.test.demo.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("****************************");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//      stomp 내부 메서드 타입에 따라 command 설정 CONNECT, SEND, PUBLISH
        if(accessor.getCommand() == StompCommand.CONNECT || accessor.getCommand() == StompCommand.SEND) {
            String header = accessor.getFirstNativeHeader("Authorization");
            log.info("Authorization header: " + header);
            if(header != null && header.startsWith("Bearer ")) {
                header = header.replace("Bearer ", "");
                return message;
            }
            if(!jwtTokenProvider.validate_token(header))
                try {
                    throw new AccessDeniedException("");
                } catch (AccessDeniedException e) {
                    throw new RuntimeException(e);
                }
        }
        return null;
    }
}
