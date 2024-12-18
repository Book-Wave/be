package com.test.demo.service.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final RedisService redisService; // RedisService 주입
    private static final String CHAT_ROOM_PREFIX = "message:";
    private final ChatService chatService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



    public List<String> findAllRoomById(String sender) throws UnsupportedEncodingException {
        sender = URLDecoder.decode(sender, "UTF-8");
        try {
            Set<String> roomKeys = redisService.keys(CHAT_ROOM_PREFIX + "*");
            log.info("Found room keys: {}", roomKeys);

            sender = URLDecoder.decode(sender, "UTF-8");
            log.info("Decoded sender: {}", sender);

            Map<String, LocalDateTime> roomLastMessageTimes = new HashMap<>();

            for (String roomKey : roomKeys) {
                log.info("Processing room: {}", roomKey);

                String users = roomKey.split(":")[1];
                String userOne = users.split("-")[0];
                String userTwo = users.split("-")[1];

                if (userOne.equals(sender) || userTwo.equals(sender)) {
                    log.info("User {} is part of room: {}", sender, roomKey);

                    // Redis에서 메시지 리스트 가져오기
                    List<ChatVO> messages = redisService.getMessageList(roomKey);
                    if (!messages.isEmpty()) {
                        String lastMessageTimeStr = messages.get(messages.size() - 1).getMessagetime();
                        LocalDateTime lastMessageTime = LocalDateTime.parse(lastMessageTimeStr, formatter);
                        roomLastMessageTimes.put(roomKey, lastMessageTime);
                    }
                }
            }

            // 마지막 메시지 시간 기준으로 정렬
            List<String> result = new ArrayList<>(roomLastMessageTimes.keySet());
            result.sort((room1, room2) -> roomLastMessageTimes.get(room2).compareTo(roomLastMessageTimes.get(room1)));

            log.info("Sorted rooms by last message time: {}", result);
            return result;

        } catch (Exception e) {
            log.error("채팅방 목록 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }


    // 특정 채팅방 조회
    // 특정 방에 접속인데 필요 ?
    public List<ChatVO> findById(String roomId) {
        try {
            roomId = URLDecoder.decode(roomId, StandardCharsets.UTF_8);
            List<ChatVO> chatMessages = redisService.getMessageList(roomId);
            if (chatMessages.isEmpty()) {
                throw new NoSuchElementException("채팅방을 찾을 수 없습니다: " + roomId);
            }
            return chatMessages;
        } catch (Exception e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 채팅방 생성
    public void createRoom(String userOne, String userTwo) throws UnsupportedEncodingException {
        userOne = URLDecoder.decode(userOne, StandardCharsets.UTF_8);
        userTwo = URLDecoder.decode(userTwo, StandardCharsets.UTF_8);
        String roomId = ChatKey.generateSortedKey("message", userOne, userTwo);
        try {
            if (redisService.exists(CHAT_ROOM_PREFIX + roomId)) {
                throw new IllegalStateException("이미 존재하는 채팅방입니다: " + roomId);
            }

            // 채팅방 초기 메시지 생성
            ChatVO chatVO = new ChatVO();
            chatVO.setRoomId(roomId);
            chatVO.setMessagetime(LocalDateTime.now().format(formatter));
            chatVO.setSender(userOne);
            chatVO.setReceiver(userTwo);
            chatVO.setMessage("방이 생성되었습니다.");
            chatVO.setRead(false);

            // 메시지 리스트 생성 및 Redis 저장
            List<ChatVO> messages = new ArrayList<>();
            messages.add(chatVO);

            redisService.setMessageList(roomId, messages, 60); // TTL 1시간
            log.info("새로운 채팅방 생성 완료: ID = {}", roomId);
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 생성 중 오류가 발생했습니다.", e);
        }
    }


    public void syncRoomsToDB() {
        Set<String> keys = redisService.keys(CHAT_ROOM_PREFIX + "*");
        for (String key : keys) {
            try {
                // Redis에서 해당 채팅방의 메시지 목록을 가져옵니다.
                List<ChatVO> messages = (List<ChatVO>) redisService.get(key);
                if (messages != null && !messages.isEmpty()) {
                    // 메시지들을 DB에 저장 (Redis -> DB 동기화)
                    for (ChatVO message : messages) {
                        chatService.saveMessage(message.getRoomId(), message.getSender(), message.getReceiver(), message.getMessage(), message.isRead());
                    }
                }
            } catch (Exception e) {
                log.error("Redis에서 DB로 채팅방 동기화 중 오류 발생: {}", e.getMessage());
            }
        }
    }
}
