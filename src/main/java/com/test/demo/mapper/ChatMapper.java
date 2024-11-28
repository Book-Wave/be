package com.test.demo.mapper;

import com.test.demo.dao.ChatDAO;
import com.test.demo.vo.ChatVO;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;


@Mapper
public interface ChatMapper {

//  전송한 메세지 저장
    void insertMessage(ChatDAO chatDAO);

//  채팅 내역 불러오기
    List<ChatVO> selectMessagesByRoomId(String roomId);

//  메세지 삭제
    void deleteMessage(String id);

}
