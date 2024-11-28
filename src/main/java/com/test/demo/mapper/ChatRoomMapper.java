package com.test.demo.mapper;


import com.test.demo.dao.ChatRoomDAO;
import com.test.demo.vo.ChatRoomVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRoomMapper {
    ChatRoomVO getChatRoomById(Long id);
    void insertChatRoom(ChatRoomVO chatRoom);
    void insertOrUpdateChatRoom(ChatRoomDAO chatRoomDAO);
}