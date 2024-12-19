package com.test.demo.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.vo.chat.ChatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final RedisTemplate<String, ChatVO> chatRedisTemplate;
    private final ChatDAO chatDAO;
    private static final int MAX_MESSAGES_COUNT = 100;
    private static final Duration SAVE_TIME = Duration.ofMinutes(10);
    private static final Duration TTL = Duration.ofMinutes(60); // TTL 설정 (60분)
    private static final Duration THRESHOLD = Duration.ofSeconds(60); // TTL 임계값 (60초)



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


    @Async
    public void setMessageList(String roomId, List<ChatVO> messages, int minutes) {
        try {
            if (!roomId.startsWith("messages:")) {
                roomId = "messages:" + roomId;
            }
            List<ChatVO> existingMessages = getMessageList(roomId);

            // 새 메시지만 추가
            for (ChatVO newMessage : messages) {
                if (!existingMessages.stream().anyMatch(m ->
                        m.getMessagetime().equals(newMessage.getMessagetime()) &&
                                m.getSender().equals(newMessage.getSender()) &&
                                m.getMessage().equals(newMessage.getMessage()))) {
                    existingMessages.add(newMessage);
                    chatRedisTemplate.opsForList().rightPush(roomId, newMessage);
                }
            }

            // MAX_MESSAGES_COUNT 초과 시 처리
            if (existingMessages.size() > MAX_MESSAGES_COUNT) {
                List<ChatVO> messagesToStore = existingMessages.subList(0, existingMessages.size() - MAX_MESSAGES_COUNT);
                chatDAO.insertBulkMessages(messagesToStore);
                existingMessages = new ArrayList<>(existingMessages.subList(existingMessages.size() - MAX_MESSAGES_COUNT, existingMessages.size()));

                // Redis 리스트 갱신
                chatRedisTemplate.delete(roomId);
                for (ChatVO message : existingMessages) {
                    chatRedisTemplate.opsForList().rightPush(roomId, message);
                }
            }

            // TTL 설정
            chatRedisTemplate.expire(roomId, Duration.ofMinutes(minutes));

            log.info("Redis에 메시지 리스트 저장 완료. Room ID: {}, 메시지 수: {}", roomId, existingMessages.size());
        } catch (Exception e) {
            log.error("Redis 리스트에 값을 저장하는 중 오류 발생: ", e);
            throw new RuntimeException("Redis 리스트에 값을 저장하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public List<ChatVO> getMessageList(String roomId) {
        try {
            
            if (roomId.startsWith("messages:messages:")) {
                roomId = roomId.replaceFirst("messages:", "");
            } else if (!roomId.startsWith("messages:")) {
                roomId = "messages:" + roomId;
            }

            log.info("roomid : " + roomId);

            List<ChatVO> redisData = chatRedisTemplate.opsForList().range(roomId, 0, -1);

            if (redisData == null || redisData.isEmpty()) {
                redisData = chatDAO.selectMessagesByRoomId(roomId);
                if (!redisData.isEmpty()) {
                    setMessageList(roomId, redisData, (int) SAVE_TIME.toMinutes());
                }
            }

            log.info("Redis에서 메시지 리스트 조회 완료. Room ID: {}, 메시지 수: {}", roomId, redisData.size());
            return redisData;
        } catch (Exception e) {
            log.error("메시지 리스트를 가져오는 중 오류 발생: ", e);
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

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkAndSaveExpiringMessages() {
        Set<String> keys = chatRedisTemplate.keys("messages:*");
        for (String key : keys) {
            Long ttl = chatRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl <= THRESHOLD.getSeconds()) {
                List<ChatVO> messages = getMessageList(key);
                if (!messages.isEmpty()) {
                    chatDAO.insertBulkMessages(messages);
                    chatRedisTemplate.delete(key);
                    log.info("메시지를 DB에 저장하고 Redis에서 삭제했습니다. Key: {}", key);
                }
            }
        }
    }
}


