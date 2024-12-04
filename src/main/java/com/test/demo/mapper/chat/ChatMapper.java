package com.test.demo.mapper.chat;

import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.vo.chat.ChatVO;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;


@Mapper
public interface ChatMapper {

//  전송한 메세지 저장
    void insertMessage(ChatVO chatVO);

//  채팅 내역 불러오기
    List<ChatVO> selectMessagesByRoomId(String roomId);

//  메세지 삭제
    void deleteMessage(long id);

}
