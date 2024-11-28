package com.test.demo.service.chat;


import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.mapper.chat.ChatMapper;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatVO;
import com.test.demo.vo.chat.MessageType;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    private final ChatMapper chatMapper;
    private final RedisService redisService;

    public ChatService(ChatMapper chatMapper, RedisService redisService) {
        this.chatMapper = chatMapper;
        this.redisService = redisService;
    }

    // 메시지 저장
    public void saveMessage(String roomId, String sender, String message, MessageType type) {
        try {
            // ChatDAO 객체 생성
            ChatDAO chatDAO = ChatDAO.builder()
                    .roomId(roomId)
                    .sender(sender)
                    .message(message)
                    .type(type)
                    .time(LocalDateTime.now())
                    .build();

            // Redis에 저장 (key는 'message:{roomId}:{sender}' 형태로)
            redisService.set("message:" + roomId + ":" + sender, chatDAO, 3600);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatVO> findMessagesByRoomId(String roomId) {
        try {
            List<ChatVO> messages = new ArrayList<>();

            // Redis에서 메시지 조회 (message:{roomId}:* 형태로)
            String pattern = "message:" + roomId + ":*";
            Set<String> keys = redisService.keys(pattern);
            for (String key : keys) {
                Object message = redisService.get(key);
                if (message instanceof ChatDAO) {
                    // ChatDAO 객체를 VO로 변환하여 리스트에 추가
                    messages.add(((ChatDAO) message).toVO());
                }
            }

            // DB에서 메시지 조회
            List<ChatVO> dbMessages = chatMapper.selectMessagesByRoomId(roomId);
            messages.addAll(dbMessages);

            // 메시지 정렬 (시간 기준)
            messages.sort(Comparator.comparing(ChatVO::getTime, Comparator.nullsLast(Comparator.naturalOrder())));

            return messages;

        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 메시지 삭제
    public void deleteMessage(String roomId, Long messageId) {
        try {
            // Redis에서 메시지 삭제
            String redisKey = "message:" + roomId + ":" + messageId;
            redisService.delete(redisKey);

            // DB에서 메시지 삭제
            chatMapper.deleteMessage(messageId);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
