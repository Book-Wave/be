package com.test.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
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
        registry.addEndpoint("/book/chat")
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//      클라이언트에서 보낸 메세지를 받은 prefix
        registry.setApplicationDestinationPrefixes("/send");
        registry.enableSimpleBroker("/room");
    }

}
