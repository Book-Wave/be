package com.test.demo.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.dao.chat.ChatRoomDAO;
import com.test.demo.vo.chat.ChatRoomVO;
import com.test.demo.vo.chat.ChatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public void set(String key, Object value, int minutes) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(minutes));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void clear() {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("*")));
    }

//  방목록 모두 불러와서 따로 출력
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    // 메세지 리스트로 저장
    public void setbyList(String key, Object value, int minutes) {
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForList().rightPush(key, serializedValue);

            // TTL 설정
            if (minutes > 0) {
                redisTemplate.expire(key, Duration.ofMinutes(minutes));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 리스트에 값을 저장하는 동안 직렬화 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Redis 리스트에 값을 저장하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 메시지 리스트 가져오기
    public List<ChatVO> getbyList(String key) {
        try {
            log.info("현재 키: {}",key);
            List<Object> redisData = redisTemplate.opsForList().range(key, 0, -1); // 리스트 전체 가져오기

            if (redisData == null || redisData.isEmpty()) {
                log.info("Redis에서 가져온 데이터가 비어 있습니다 - Key: {}", key);
                return Collections.emptyList(); // 빈 리스트 반환
            }

            log.info("Fetched data from Redis for key '{}': {}", key, redisData);

            // Redis에서 가져온 데이터를 ChatVO로 역직렬화
            return redisData.stream()
                    .map(data -> {
                        try {
                            // 직렬화된 JSON 문자열을 ChatVO 객체로 역직렬화
                            return objectMapper.readValue((String) data, ChatVO.class); // 직접 캐스팅하여 객체로 변환
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Redis 리스트 데이터 변환 중 오류가 발생했습니다: " + e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Redis 리스트 데이터를 가져오는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

}
