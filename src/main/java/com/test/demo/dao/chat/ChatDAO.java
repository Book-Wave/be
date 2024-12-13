package com.test.demo.dao.chat;

import com.test.demo.mapper.chat.ChatMapper;
import com.test.demo.vo.chat.ChatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChatDAO {

    // chatMapper를 인스턴스 변수로 변경하고, @Autowired로 의존성 주입 받기
    @Autowired
    private ChatMapper chatMapper;

    // 전송한 메시지 저장
    public void insertMessage(ChatVO chatVO) {
        if (chatMapper != null) {
            chatMapper.insertMessage(chatVO);
        } else {
            // chatMapper가 null일 때의 처리
            System.out.println("chatMapper is null, skipping insert");
        }
    }

    // 메시지 삭제
    public void deleteMessage(String roomId,String roomDate) {
        if (chatMapper != null) {
            chatMapper.deleteMessage(roomId,roomDate);
        } else {
            // chatMapper가 null일 때의 처리
            System.out.println("chatMapper is null, skipping delete");
        }
    }

    // 메시지 읽음 상태 업데이트
    public void updateMessageReadStatus(Map<String, Object> params) {
        if (chatMapper != null) {
            chatMapper.updateMessageReadStatus(params);
        } else {
            System.out.println("chatMapper is null, skipping read status update");
        }
    }


    // 채팅 내역 불러오기
    public List<ChatVO> selectMessagesByRoomId(String roomId) {
        if (chatMapper != null) {

            return chatMapper.selectMessagesByUserId(roomId);
        } else {
            // chatMapper가 null일 때, 빈 리스트 반환
            System.out.println("chatMapper is null, returning empty list");
            return Collections.emptyList();
        }
    }





}
