package com.test.demo.dao;

import com.test.demo.vo.ChatRoomVO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChatRoomDAO {
    private String roomId;
    private String roomName;
    private LocalDate roomDate;

    // VO에서 DAO로 변환하는 메서드
    public static ChatRoomDAO fromVO(ChatRoomVO chatRoomVO) {
        ChatRoomDAO dao = new ChatRoomDAO();
        dao.setRoomId(chatRoomVO.getRoomId());
        dao.setRoomName(chatRoomVO.getRoomName());
        dao.setRoomDate(chatRoomVO.getRoomDate());
        return dao;
    }

    // DAO에서 VO로 변환하는 메서드
    public ChatRoomVO toVO() {
        return new ChatRoomVO(roomId, roomName, roomDate);
    }
}
