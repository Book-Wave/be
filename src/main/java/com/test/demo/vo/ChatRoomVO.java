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
    private String userone;
    private String usertwo;
    private LocalDate roomDate;


    public static ChatRoomVO create(String userone, String usertwo) {
        ChatRoomVO room = new ChatRoomVO();
        room.roomId = UUID.randomUUID().toString(); // UUID로 고유 ID 생성
        room.userone = userone;
        room.usertwo = usertwo;
        room.roomDate = LocalDate.now();
        return room;
    }
}
