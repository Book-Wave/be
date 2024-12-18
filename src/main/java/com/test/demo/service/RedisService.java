package com.test.demo.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.vo.chat.ChatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final RedisTemplate<String, ChatVO> chatRedisTemplate;
    private final ChatDAO chatDAO;
    private static final int MAX_MESSAGES_COUNT = 100;
    private static final Duration SAVE_TiME = Duration.ofMinutes(10);
    private final Map<String, LocalDateTime> lastSaveTimes = new HashMap<>();


    public void set(String key, Object value, int minutes) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(minutes));
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis 데이터 읽기 중 오류 발생. Key: {}, Error: {}", key, e.getMessage());
            throw new RuntimeException("Redis 데이터 읽기 중 오류 발생: " + e.getMessage(), e);
        }
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


    public void setMessageList(String roomId, List<ChatVO> messages, int minutes) {
        try {
            List<ChatVO> existingMessages = getMessageList(roomId);
            existingMessages.addAll(messages);

            LocalDateTime now = LocalDateTime.now();
            boolean shouldSave = existingMessages.size() > MAX_MESSAGES_COUNT ||
                    !lastSaveTimes.containsKey(roomId) ||
                    Duration.between(lastSaveTimes.getOrDefault(roomId, now.minusDays(1)), now).compareTo(SAVE_TiME) > 0;

            if (shouldSave) {
                if (existingMessages.size() > MAX_MESSAGES_COUNT) {
                    List<ChatVO> messagesToStore = existingMessages.subList(0, existingMessages.size() - MAX_MESSAGES_COUNT);
                    chatDAO.insertBulkMessages(messagesToStore);
                    existingMessages = new ArrayList<>(existingMessages.subList(existingMessages.size() - MAX_MESSAGES_COUNT, existingMessages.size()));
                } else {
                    chatDAO.insertBulkMessages(existingMessages);
                }
                lastSaveTimes.put(roomId, now);
            }

            chatRedisTemplate.delete(roomId);
            for (ChatVO message : existingMessages) {
                chatRedisTemplate.opsForList().rightPush(roomId, message);
            }

            chatRedisTemplate.expire(roomId, Duration.ofMinutes(minutes));
            log.info("Redis에 메시지 리스트 저장 완료. Room ID: {}, 메시지 수: {}", roomId, existingMessages.size());
        } catch (Exception e) {
            log.error("Redis 리스트에 값을 저장하는 중 오류 발생: ", e);
            throw new RuntimeException("Redis 리스트에 값을 저장하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // Redis에서 메시지 리스트 가져오기
    public List<ChatVO> getMessageList(String roomId) {
        try {
            List<ChatVO> redisData = chatRedisTemplate.opsForList().range(roomId, 0, -1);
            if (redisData == null || redisData.isEmpty()) {
                redisData = chatDAO.selectMessagesByRoomId(roomId);
                if (!redisData.isEmpty()) {
                    setMessageList(roomId, redisData, 60); // 1시간 동안 캐시
                }
            }
            return redisData;
        } catch (Exception e) {
            throw new RuntimeException("메시지 리스트를 가져오는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }


    public void updateMessageReadStatusInRedis(String roomId, List<Integer> messageIds) {
        try {
            // Redis에서 메시지 리스트 가져오기
            List<ChatVO> messages = getMessageList(roomId);

            // 읽음 상태 업데이트
            boolean updated = false;
            for (ChatVO message : messages) {
                if (messageIds.contains(message.getMessageId())) {
                    message.setRead(true); // 읽음 처리
                    updated = true;
                }
            }

            if (updated) {
                // 기존 리스트 삭제 후 업데이트된 리스트 저장
                chatRedisTemplate.delete(roomId);
                for (ChatVO updatedMessage : messages) {
                    chatRedisTemplate.opsForList().rightPush(roomId, updatedMessage);
                }

                log.info("Redis에 읽음 상태가 업데이트되었습니다. Room ID: {}, Message IDs: {}", roomId, messageIds);
            } else {
                log.info("업데이트할 메시지가 없습니다. Room ID: {}, Message IDs: {}", roomId, messageIds);
            }
        } catch (Exception e) {
            throw new RuntimeException("Redis 메시지 읽음 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}


