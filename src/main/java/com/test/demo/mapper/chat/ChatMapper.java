package com.test.demo.mapper.chat;

import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.vo.chat.ChatVO;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
import java.util.Map;


@Mapper
public interface ChatMapper {

//  전송한 메세지 저장
    void insertMessage(ChatVO chatVO);

//  채팅 내역 불러오기
    List<ChatVO> selectMessagesByUserId(String roomId);

//  메세지 삭제
    void deleteMessage(String roomId, String roomDate);

    // 메시지 읽음 상태 업데이트
    void updateMessageReadStatus(Map<String, Object> params);

    void insertBulkMessages(List<ChatVO> messages);


}
