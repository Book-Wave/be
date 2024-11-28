package com.test.demo.mapper.chat;


import com.test.demo.dao.chat.ChatRoomDAO;
import com.test.demo.vo.chat.ChatRoomVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRoomMapper {
    ChatRoomVO getChatRoomById(Long id);
    void insertChatRoom(ChatRoomVO chatRoom);
    void insertOrUpdateChatRoom(ChatRoomDAO chatRoomDAO);
}