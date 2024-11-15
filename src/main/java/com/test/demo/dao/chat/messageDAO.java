package com.test.demo.dao.chat;

import lombok.Builder;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;


public class messageDAO {


    private Long id; // 채팅 ID (자동 생성될 것임)
    private Long roomId; // 방 ID
    private String sender;
    private String senderEmail;
    private String message;
    private LocalDateTime sendDate;

    @Builder
    public messageDAO(Long roomId, String sender, String senderEmail, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }

    public static messageDAO createChat(Long roomId, String sender, String senderEmail, String message) {
        return messageDAO.builder()
                .roomId(roomId)
                .sender(sender)
                .senderEmail(senderEmail)
                .message(message)
                .build();
    }
}
