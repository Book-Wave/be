package com.test.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTest {

    private final RedisTemplate<String, Object> redisTemplate;


    public void testRedis() {
        // Redis에 값 저장 및 조회
        redisTemplate.opsForValue().set("testKey", "Hello, Redis!");
        String value = (String) redisTemplate.opsForValue().get("testKey");
        log.info("Stored value in Redis: " + value); // "Hello, Redis!" 출력
    }
}