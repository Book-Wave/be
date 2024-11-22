package com.test.demo.vo;


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
    private String roomName;
    private LocalDate roomDate;


    public static ChatRoomVO create(String name) {
        ChatRoomVO room = new ChatRoomVO();
        room.roomId = UUID.randomUUID().toString();
        room.roomName = name;
        room.roomDate = LocalDate.now();
        return room;
    }
}
