package com.test.demo.service.chat;



import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatVO;
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
    public void saveMessage(String roomId, String sender, String receiver, String message, boolean isread) {
        try {
            // ChatVO 객체 생성
            String roomDate = LocalDateTime.now().format(formatter);
            ChatVO chatVO = ChatVO.builder()
                    .roomId(roomId)
                    .sender(sender)
                    .receiver(receiver)
                    .message(message)
                    .messagetime(roomDate)
                    .isRead(isread)
                    .build();


            // Redis에 저장 -> roomId를 sender-recevier로 저장
            redisService.setMessageList(roomId,chatVO, 3600);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatVO> findMessagesByRoomId(String roomId) {
        try {

            // Redis에서 메시지 조회 (message:{roomId}:* 형태로)
            String pattern = "message:" + roomId;
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
        }
    }

    // 메시지 삭제
    public void deleteMessage(String roomId, String roomDate) {
        try {
            // Redis에서 roomId로 메시지 목록을 가져옴
            String redisKey = "message:" + roomId;
            List<ChatVO> chatList = redisService.getMessageList(redisKey);

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
                redisService.setMessageList(redisKey, chatList,3600);
            }

            // DB에서 해당 메시지 삭제
            chatDAO.deleteMessage(roomId, roomDate);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
