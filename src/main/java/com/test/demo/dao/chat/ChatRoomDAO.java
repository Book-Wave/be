package com.test.demo.dao.chat;

import com.test.demo.mapper.chat.ChatRoomMapper;
import com.test.demo.vo.chat.ChatRoomVO;
import org.springframework.stereotype.Repository;




@Repository
public class ChatRoomDAO {

    private final ChatRoomMapper chatRoomMapper;


    public ChatRoomDAO(ChatRoomMapper chatRoomMapper) {
        this.chatRoomMapper = chatRoomMapper;
    }

    //  전송한 메세지 저장
    public void insertOrUpdateChatRoom(ChatRoomVO chatRoomVO) {chatRoomMapper.insertOrUpdateChatRoom(chatRoomVO);};




}
