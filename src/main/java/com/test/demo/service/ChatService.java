package com.test.demo.service;

import com.test.demo.dao.ChatDAO;
import com.test.demo.vo.ChatVO;
import com.test.demo.mapper.ChatMapper;
import com.test.demo.vo.MessageType;
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
            ChatDAO chatDAO = ChatDAO.builder()
                    .roomId(roomId)
                    .sender(sender)
                    .message(message)
                    .type(type)  // 공통 MessageType 사용
                    .time(LocalDateTime.now())
                    .build();
            redisService.set("message:"+roomId+ ":"+chatDAO.getSender(),chatDAO, 3600);

        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatVO> findMessagesByRoomId(String roomId) {
        try {
            List<ChatVO> messages = new ArrayList<>();

            String pattern = "message:"+roomId;
            Set<String> keys = redisService.keys(pattern);
            for (String key : keys) {
                Object message = redisService.get(key);
                System.out.println("Redis message: " + message);
                if(message instanceof ChatVO) {
                    messages.add((ChatVO) message);
                }

            }

            List<ChatVO> dbMessage = chatMapper.selectMessagesByRoomId(roomId);
            System.out.println("DB messages: " + dbMessage);
            messages.addAll(dbMessage);

//          정렬
            messages.sort(Comparator.comparing(ChatVO::getTime, Comparator.nullsLast(Comparator.naturalOrder())));

            return messages;

        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 메시지 삭제
    public void deleteMessage(String roomId, Long messageId) {
        try {
            // Redis에서 삭제
            String redisKey = roomId + ":" + messageId;
            redisService.delete(redisKey);

            // DB에서 삭제
            chatMapper.deleteMessage(roomId);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
