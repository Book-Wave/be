package com.test.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.vo.chat.ChatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}") private String host;
    @Value("${spring.data.redis.port}") private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }


    // 기본 RedisTemplate<String, Object> 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 키는 String으로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 기본 값은 Jackson2JsonRedisSerializer 사용
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        valueSerializer.setObjectMapper(objectMapper());

        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        log.info("기본 RedisTemplate 설정 완료: ValueSerializer - Jackson2JsonRedisSerializer");

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, ChatVO> chatRedisTemplate() {
        RedisTemplate<String, ChatVO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 키는 String으로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // ChatVO에 맞는 ObjectMapper 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 모듈
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식 사용

        // ChatVO에 맞는 직렬화 설정
        Jackson2JsonRedisSerializer<ChatVO> valueSerializer = new Jackson2JsonRedisSerializer<>(ChatVO.class);
        valueSerializer.setObjectMapper(objectMapper);

        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        log.info("ChatVO 전용 RedisTemplate 설정 완료");

        return redisTemplate;
    }





    // ObjectMapper 설정 (공통적으로 사용)
    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 모듈
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식 사용
        return objectMapper;
    }
}
