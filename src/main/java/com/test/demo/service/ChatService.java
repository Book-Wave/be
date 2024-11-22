package com.test.demo.service;

import com.test.demo.dao.ChatDAO;
import com.test.demo.vo.ChatVO;
import com.test.demo.mapper.ChatMapper;
import com.test.demo.vo.MessageType;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMapper chatMapper;

    public ChatService(ChatMapper chatMapper) {
        this.chatMapper = chatMapper;
    }

    // 메시지 저장
    public void saveMessage(String roomId, String sender, String message, MessageType type) {
        try {
            ChatDAO chatDAO = new ChatDAO();
            chatDAO.setRoomId(roomId);
            chatDAO.setSender(sender);
            chatDAO.setMessage(message);
            chatDAO.setType(type);  // 공통 MessageType 사용
            chatDAO.setTime(LocalDateTime.now());

            chatMapper.insertMessage(chatDAO); // 메시지 저장
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatVO> findMessagesByRoomId(String roomId) {
        try {
            return chatMapper.selectMessagesByRoomId(roomId);
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 메시지 삭제
    public void deleteMessage(Long id) {
        try {
            chatMapper.deleteMessage(id); // 메시지 삭제
        } catch (DataAccessException e) {
            throw new RuntimeException("메시지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
