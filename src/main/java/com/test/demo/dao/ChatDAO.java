package com.test.demo.dao;

import com.test.demo.vo.ChatVO;
import com.test.demo.vo.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDAO {

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime time;

    // VO에서 DAO로 변환하는 메서드
    public static ChatDAO fromVO(ChatVO chatVO) {
        ChatDAO dao = new ChatDAO();
        dao.setType(chatVO.getType());
        dao.setRoomId(chatVO.getRoomId());
        dao.setSender(chatVO.getSender());
        dao.setMessage(chatVO.getMessage());
        dao.setTime(chatVO.getTime());
        return dao;
    }

    // DAO에서 VO로 변환하는 메서드
    public ChatVO toVO() {
        return new ChatVO(type, roomId, sender, message, time);
    }
}
