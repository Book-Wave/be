package com.test.demo.dao.chat;

import com.test.demo.vo.chat.ChatVO;
import com.test.demo.vo.chat.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatDAO {
    private int messageId;
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime time;



    // VO에서 DAO로 변환하는 메서드
    public ChatDAO fromVO(ChatVO chatVO) {
        return ChatDAO.builder()
                .messageId(chatVO.getMessageId())
                .type(chatVO.getType())
                .roomId(chatVO.getRoomId())
                .sender(chatVO.getSender())
                .message(chatVO.getMessage())
                .time(chatVO.getTime())
                .build();
    }

    // DAO에서 VO로 변환하는 메서드
    public ChatVO toVO() {
        return ChatVO.builder()
                .messageId(messageId)
                .type(type)
                .roomId(roomId)
                .sender(sender)
                .message(message)
                .time(time)
                .build();
    }
}
