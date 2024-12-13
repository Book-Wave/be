package com.test.demo.vo.chat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatVO {
    private int messageId; // 메시지 ID (고유값)
    private MessageType type; // 메시지 타입
    private String roomId;    // 방 ID
    private String sender;    // 보낸 사람
    private String message;   // 메시지 내용
    private String time; // 메시지 전송 시간
}
