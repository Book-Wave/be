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
    private static final String CHAT_ROOM_PREFIX = "messages:";
    private final ChatService chatService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;



    public List<String> findAllRoomById(String sender) throws UnsupportedEncodingException {
        sender = URLDecoder.decode(sender, "UTF-8");
        try {
            Set<String> roomKeys = redisService.keys(CHAT_ROOM_PREFIX + "*");
            log.info("Found room keys: {}", roomKeys);
            log.info("Decoded sender: {}", sender);

            Map<String, LocalDateTime> roomLastMessageTimes = new HashMap<>();

            for (String roomKey : roomKeys) {
                log.info("Processing room: {}", roomKey);

                String[] parts = roomKey.split(":");
                if (parts.length < 2) {
                    log.warn("Invalid room key format: {}", roomKey);
                    continue;
                }

                String users = parts[1];
                String[] userParts = users.split("-");
                if (userParts.length < 2) {
                    log.warn("Invalid users format in room key: {}", users);
                    continue;
                }

                String userOne = userParts[0];
                String userTwo = userParts[1];

                if (userOne.equals(sender) || userTwo.equals(sender)) {
                    log.info("User {} is part of room: {}", sender, roomKey);

                    List<ChatVO> messages = redisService.getMessageList(roomKey);
                    if (!messages.isEmpty()) {
                        String lastMessageTimeStr = messages.get(messages.size() - 1).getMessagetime();
                        LocalDateTime lastMessageTime = LocalDateTime.parse(lastMessageTimeStr, formatter);
                        roomLastMessageTimes.put(roomKey, lastMessageTime);
                    }
                }
            }

            List<String> result = new ArrayList<>(roomLastMessageTimes.keySet());
            result.sort((room1, room2) -> roomLastMessageTimes.get(room2).compareTo(roomLastMessageTimes.get(room1)));

            log.info("Sorted rooms by last message time: {}", result);
            return result;

        } catch (Exception e) {
            log.error("채팅방 목록 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }


    // 채팅방 생성
    public void createRoom(String userOne, String userTwo) throws UnsupportedEncodingException {
        userOne = URLDecoder.decode(userOne, StandardCharsets.UTF_8);
        userTwo = URLDecoder.decode(userTwo, StandardCharsets.UTF_8);
        String roomId = ChatKey.generateSortedKey("messages", userOne, userTwo);
        try {
            if (redisService.exists(roomId)) {
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


}
