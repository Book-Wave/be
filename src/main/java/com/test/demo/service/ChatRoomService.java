package com.test.demo.service;

import com.test.demo.dao.ChatRoomDAO;
import com.test.demo.vo.ChatRoomVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private Map<String, ChatRoomDAO> chatRooms;

    @PostConstruct
    //실행 시 생성
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 불러오기
    public List<ChatRoomVO> findAllRoom() {
        try {
            // 채팅방 최근 생성 순으로 반환
            List<ChatRoomVO> result = new ArrayList<>();
            for (ChatRoomDAO roomDAO : chatRooms.values()) {
                result.add(roomDAO.toVO());  // DAO에서 VO로 변환
            }
            Collections.reverse(result);
            return result;
        } catch (Exception e) {
            log.error("채팅방 목록 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    //채팅방 하나 불러오기
    public ChatRoomVO findById(String roomId) {
        try {
            ChatRoomDAO roomDAO = chatRooms.get(roomId);
            if (roomDAO == null) {
                log.warn("채팅방 ID가 {}인 채팅방을 찾을 수 없습니다.", roomId);
                throw new NoSuchElementException("채팅방을 찾을 수 없습니다: " + roomId);
            }
            return roomDAO.toVO();
        } catch (NoSuchElementException e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage());
            throw e; // 이 예외는 클라이언트에게 전달될 수 있도록 그대로 던집니다.
        } catch (Exception e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방을 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    //채팅방 생성
    public ChatRoomVO createRoom(String name) {
        try {
            ChatRoomDAO chatRoomDAO = ChatRoomDAO.fromVO(ChatRoomVO.create(name));
            log.info("Adding chat room: ID = {}, Name = {}", chatRoomDAO.getRoomId(), chatRoomDAO.getRoomName());
            chatRooms.put(chatRoomDAO.getRoomId(), chatRoomDAO);
            return chatRoomDAO.toVO();
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 생성 중 오류가 발생했습니다.", e);
        }
    }
}
