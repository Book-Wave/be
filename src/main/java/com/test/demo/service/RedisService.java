package com.test.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.dao.ChatRoomDAO;
import com.test.demo.vo.ChatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, int minutes) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(minutes));
    }

    public Object get(String key) {
        Object data = redisTemplate.opsForValue().get(key);

        // "chatroom:{roomId}" 형식일 경우 ChatRoomDAO로 변환
        if (key.startsWith("chatroom:")) {
            if (data instanceof LinkedHashMap) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());  // Java 8 시간 모듈 등록
                return objectMapper.convertValue(data, ChatRoomDAO.class); // ChatRoomDAO로 변환
            }
        }

        // "message:{roomId}:{sender}" 형식일 경우 ChatVO로 변환
        else if (key.startsWith("message:")) {
            if (data instanceof LinkedHashMap) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());  // Java 8 시간 모듈 등록
                return objectMapper.convertValue(data, ChatVO.class); // ChatVO로 변환
            }
        }

        // 다른 경우는 그대로 반환
        return data;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void clear () {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("*")));
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

}
