package com.test.demo.config;

import com.test.demo.config.jwt.JwtHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.*;

@Configuration
//stomp 사용을 위한 어노테이션
//stomp는 메세지 전송을 효율적으로 하기위한 프로토콜 pub/sub 구조
//websocket위에서 작동하는 프로토콜, 클라이언트와 서버가 전송할 메세지들을 정의한다.
@EnableWebSocketMessageBroker
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*");


    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

//      여기 모드 클라이언트 -> 서버의 url 경로를 설정한다
//      publisher -> messagebroker -> subscriber 구성
//      순서가 1. 구독 2. 메세지 발생 -> 해당 roomId에 메세지를 보낸다 -> convertandsend 등으로 보낸다

        registry.enableSimpleBroker( "/sub");
        registry.setApplicationDestinationPrefixes("/pub");

    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(8192)  // 메시지 크기 제한
                .setSendBufferSizeLimit(8192)  // 버퍼 크기 제한
                .setSendTimeLimit(10000);  // 전송 시간 제한
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("STOMP Connection attempt: {}", accessor.getSessionId());
                }
                return message;
            }
        });
    }

}
