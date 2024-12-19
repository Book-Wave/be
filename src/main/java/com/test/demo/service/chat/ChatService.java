package com.test.demo.service.chat;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatDAO chatDAO;
    private final RedisService redisService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



    public void saveMessage(String roomId, String sender, String receiver, String message, boolean isRead) {
        try {
            if (!roomId.startsWith("messages:")) {
                roomId = "messages:" + URLDecoder.decode(roomId, "UTF-8");
            }
            String roomDate = LocalDateTime.now().format(formatter);
            ChatVO chatVO = ChatVO.builder()
                    .roomId(roomId)
                    .sender(sender)
                    .receiver(receiver)
                    .message(message)
                    .messagetime(roomDate)
                    .isRead(isRead)
                    .build();

            List<ChatVO> messages = new ArrayList<>();
            messages.add(chatVO);
            redisService.setMessageList(roomId, messages, 60);

            log.info("메시지 저장 완료. Room ID: {}, Sender: {}, Message: {}", roomId, sender, message);
        } catch (Exception e) {
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatVO> findMessagesByRoomId(String roomId) {
        try {

            roomId = URLDecoder.decode(roomId, "UTF-8");
            // Redis에서 메시지 조회 (message:{roomId}:* 형태로)
            String pattern = "messages:" + roomId;
            List<ChatVO> messages =  redisService.getMessageList(pattern);

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
            messages.sort(Comparator.comparing(ChatVO::getMessagetime, Comparator.nullsLast(Comparator.naturalOrder())));

            return messages;

        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // 메시지 삭제
    public void deleteMessage(String roomId, String roomDate) {
        try {
            if (!roomId.startsWith("messages:")) {
                roomId = "messages:" + URLDecoder.decode(roomId, "UTF-8");
            }
            // Redis에서 roomId로 메시지 목록을 가져옴
            List<ChatVO> chatList = redisService.getMessageList(roomId);

            // roomDate에 해당하는 메시지 찾기
            ChatVO messageToDelete = null;
            for (ChatVO message : chatList) {
                if (message.getMessagetime().equals(roomDate)) {
                    messageToDelete = message;
                    break;  // 일치하는 메시지를 찾으면 반복 종료
                }
            }

            // 해당 메시지가 존재하면 삭제
            if (messageToDelete != null) {
                chatList.remove(messageToDelete);
                // Redis에 업데이트된 목록을 저장
                redisService.setMessageList(roomId, chatList,60);
            }

            // DB에서 해당 메시지 삭제
            chatDAO.deleteMessage(roomId, roomDate);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void markMessagesAsRead(String roomId, List<Integer> messageIds, String receiver) {
        try {
            if (!roomId.startsWith("messages:")) {
                roomId = "messages:" + URLDecoder.decode(roomId, "UTF-8");
            }
            // 1. Redis 업데이트
            redisService.updateMessageReadStatusInRedis(roomId, messageIds);

            // 2. DB 업데이트
            Map<String, Object> params = new HashMap<>();
            params.put("messageIds", messageIds);
            params.put("receiver", receiver);
            chatDAO.updateMessageReadStatus(params);

            log.info("메시지 읽음 상태가 업데이트되었습니다. Room ID: {}, Message IDs: {}", roomId, messageIds);
        } catch (Exception e) {
            throw new RuntimeException("메시지 읽음 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }


}
