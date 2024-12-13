package com.test.demo.service.chat;

import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.chat.ChatVO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MessageService {

    private final RedisService redisService;
    private final ChatDAO chatDAO;

    public MessageService(RedisService redisService, ChatDAO chatDAO) {
        this.redisService = redisService;
        this.chatDAO = chatDAO;
    }


    public void redisToDB() {
        String pattern = "roomId:*";
        Set<String> keys = redisService.keys(pattern);

        for (String key : keys) {
            Object message = redisService.get(key);
            if(message instanceof ChatVO) {
                ChatVO chatVO = (ChatVO) message;
                chatDAO.insertMessage(chatVO);
                redisService.delete(key);
            }
        }
    }
}
