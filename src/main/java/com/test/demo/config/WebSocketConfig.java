package com.test.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
//stomp 사용을 위한 어노테이션
//stomp는 메세지 전송을 효율적으로 하기위한 프로토콜 pub/sub 구조
//websocket위에서 작동하는 프로토콜, 클라이언트와 서버가 전송할 메세지들을 정의한다.
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//      websocket url용 endpoint
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

//      여기 모드 클라이언트 -> 서버의 url 경로를 설정한다
//      publisher -> messagebroker -> subscriber 구성
//      순서가 1. 구독 2. 메세지 발생 -> 해당 roomId에 메세지를 보낸다 -> convertandsend 등으로 보낸다

        registry.enableSimpleBroker( "/sub");
        registry.setApplicationDestinationPrefixes("/pub");

    }

}
