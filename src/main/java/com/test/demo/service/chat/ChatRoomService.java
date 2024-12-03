package com.test.demo.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.dao.chat.ChatRoomDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final RedisService redisService; // RedisService 주입
    private static final String CHAT_ROOM_PREFIX = "chatroom:";
    private final ChatRoomDAO chatRoomDAO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //채팅방 불러오기
    public List<ChatRoomVO> findAllRoom() {
        try {
            List<ChatRoomVO> result = new ArrayList<>();
            Set<String> keys = redisService.keys(CHAT_ROOM_PREFIX + "*");
            for (String key : keys) {
                // Redis에서 가져온 데이터를 Map으로 읽고 ChatRoomVO로 변환
                Object redisData = redisService.get(key);
                if (redisData != null) {
                    // ObjectMapper를 사용해 LinkedHashMap을 ChatRoomVO로 변환

                    ChatRoomVO roomVO = new ObjectMapper().convertValue(redisData, ChatRoomVO.class);
                    result.add(roomVO);
                }
            }
            Collections.reverse(result); // 최근 생성 순으로 정렬
            return result;
        } catch (Exception e) {
            log.error("채팅방 목록 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    //채팅방 하나 불러오기
    public ChatRoomVO findById(String roomId) {
        try {

            Object redisData = redisService.get(CHAT_ROOM_PREFIX + roomId);

            if (redisData == null) {
                throw new NoSuchElementException("채팅방을 찾을 수 없습니다: " + roomId);
            }
            ChatRoomVO roomVO = new ObjectMapper().convertValue(redisData, ChatRoomVO.class);
            return roomVO;
        } catch (Exception e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 조회 중 오류가 발생했습니다.", e);
        }
    }


    //채팅방 생성
    public void createRoom(String roomname, String userone, String usertwo) {
        try {
            if(usertwo == null || usertwo.isEmpty()) {
                usertwo = "";
            }

            String roomDate = LocalDateTime.now().format(formatter);
            ChatRoomVO roomVO = new ChatRoomVO(UUID.randomUUID().toString(),roomname, userone, usertwo , roomDate);

            log.info("새로운 채팅방 생성: ID = {}", roomVO.getRoomId());
            redisService.set(CHAT_ROOM_PREFIX + roomVO.getRoomId(), roomVO, 3600); // Redis에 저장 (TTL 1시간)
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 생성 중 오류가 발생했습니다.", e);
        }
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncRoomsToDB() {
        Set<String> keys = redisService.keys(CHAT_ROOM_PREFIX + "*");
        for (String key : keys) {
            try {
                ChatRoomVO roomVO = (ChatRoomVO) redisService.get(key);
                if (roomVO != null) {
                    // MyBatis 매퍼를 통해 DB에 저장
                    chatRoomDAO.insertOrUpdateChatRoom(roomVO);
                    log.info("채팅방 동기화 완료: {}", roomVO.getRoomId());
                }
            } catch (Exception e) {
                log.error("Redis에서 DB로 채팅방 동기화 중 오류 발생: {}", e.getMessage());
            }
        }
    }
}
