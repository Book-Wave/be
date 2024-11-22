package com.test.demo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatVO {


    private MessageType type;

    private String roomId;

    private String sender;

    private String message;

    private LocalDateTime time;
}
