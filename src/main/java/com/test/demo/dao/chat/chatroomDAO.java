package com.test.demo.dao.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
public class chatroomDAO {
    private String roomID;
    private String roomName;

    @Builder
    public chatroomDAO(String roomName) {
        this.roomName = roomName;
    }

    public static chatroomDAO create(String roomName) {
        chatroomDAO chatroom =new chatroomDAO();
        chatroom.roomID = UUID.randomUUID().toString();
        return chatroom;
    }
}
