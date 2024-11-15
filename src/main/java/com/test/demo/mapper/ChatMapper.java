package com.test.demo.mapper;

import com.test.demo.dao.chat.chatroomDAO;
import com.test.demo.dao.chat.messageDAO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChatMapper {

    @Insert("INSERT INTO chats (room_id, sender, sender_email, message, send_date) " +
            "VALUES (#{roomId}, #{sender}, #{senderEmail}, #{message}, #{sendDate})")
    void insertChat(messageDAO chat);

    @Insert("INSERT INTO chat_rooms (name) VALUES (#{name})")
    void insertChatRoom(chatroomDAO chatRoom);

    @Select("SELECT * FROM chat_rooms WHERE chatRoom_id = #{id}")
    chatroomDAO selectChatRoomById(Long id);

}
