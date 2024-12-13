package com.test.demo.vo.chat;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomVO {
    private String roomId;
    private String userone;
    private String usertwo;
    private String roomDate;
}
