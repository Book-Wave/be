package com.test.demo.service.chat;

import com.test.demo.dao.chat.ChatRoomDAO;
import com.test.demo.mapper.chat.ChatRoomMapper;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final RedisService redisService; // RedisService 주입
    private static final String CHAT_ROOM_PREFIX = "chatroom:";
    private final ChatRoomMapper chatRoomMapper;

    //채팅방 불러오기
    public List<ChatRoomVO> findAllRoom() {
        try {
            List<ChatRoomVO> result = new ArrayList<>();
            Set<String> keys = redisService.keys(CHAT_ROOM_PREFIX + "*");
            for (String key : keys) {
                ChatRoomDAO roomDAO = (ChatRoomDAO) redisService.get(key);  // 변환된 ChatRoomDAO 객체
                if (roomDAO != null) {
                    result.add(roomDAO.toVO());
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
            ChatRoomDAO roomDAO = (ChatRoomDAO) redisService.get(CHAT_ROOM_PREFIX + roomId);
            if (roomDAO == null) {
                throw new NoSuchElementException("채팅방을 찾을 수 없습니다: " + roomId);
            }
            return roomDAO.toVO();
        } catch (Exception e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 조회 중 오류가 발생했습니다.", e);
        }
    }

    //채팅방 생성
    public ChatRoomVO createRoom(String name, String nametwo) {
        try {
            if(nametwo == null || nametwo.isEmpty()) {
                nametwo = "";
            } else {
                ChatRoomVO roomVO = ChatRoomVO.create(name, nametwo);
            }
            ChatRoomVO roomVO = ChatRoomVO.create(name, nametwo);
            ChatRoomDAO roomDAO = ChatRoomDAO.fromVO(roomVO);

            log.info("새로운 채팅방 생성: ID = {}", roomDAO.getRoomId());
            redisService.set(CHAT_ROOM_PREFIX + roomDAO.getRoomId(), roomDAO, 3600); // Redis에 저장 (TTL 1시간)
            return roomVO;
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
                ChatRoomDAO roomDAO = (ChatRoomDAO) redisService.get(key);
                if (roomDAO != null) {
                    // MyBatis 매퍼를 통해 DB에 저장
                    chatRoomMapper.insertOrUpdateChatRoom(roomDAO);
                    log.info("채팅방 동기화 완료: {}", roomDAO.getRoomId());
                }
            } catch (Exception e) {
                log.error("Redis에서 DB로 채팅방 동기화 중 오류 발생: {}", e.getMessage());
            }
        }
    }
}
