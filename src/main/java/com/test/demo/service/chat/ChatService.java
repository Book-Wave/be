package com.test.demo.service.chat;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatVO;
import com.test.demo.vo.chat.MessageType;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChatService {

    private final ChatDAO chatDAO;
    private final RedisService redisService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ChatService(ChatDAO chatDAO, RedisService redisService, DateTimeFormatter formatter) {
        this.chatDAO = chatDAO;
        this.redisService = redisService;
        this.formatter = formatter;
    }



    // 메시지 저장
    public void saveMessage(String roomId, String sender, String message, MessageType type) {
        try {
            // ChatVO 객체 생성
            String roomDate = LocalDateTime.now().format(formatter);
            ChatVO chatVO = ChatVO.builder()
                    .roomId(roomId)
                    .sender(sender)
                    .message(message)
                    .type(type)
                    .time(roomDate)
                    .build();

            // Redis에 저장 (key는 'message:{roomId}:{sender}' 형태로)
            redisService.setbyList("message:" + roomId + ":" + sender, chatVO, 3600);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatVO> findMessagesByRoomId(String roomId, String sender) {
        try {

            // Redis에서 메시지 조회 (message:{roomId}:* 형태로)
            String pattern = "message:" + roomId + ":"+sender;
            List<ChatVO> messages =  redisService.getbyList(pattern);

            if (messages == null) {
                messages = new ArrayList<>();
            }


            // DB에서 메시지 조회
            List<ChatVO> dbMessages = chatDAO.selectMessagesByRoomId(roomId);
            if (dbMessages == null) {
                dbMessages = new ArrayList<>();  // null일 경우 빈 리스트 할당
            }
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
            chatDAO.deleteMessage(messageId);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
