package com.test.demo.service.chat;

import com.test.demo.dao.chat.ChatDAO;
import com.test.demo.mapper.chat.ChatMapper;
import com.test.demo.service.RedisService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MessageService {

    private final RedisService redisService;
    private final ChatMapper chatMapper;

    public MessageService(RedisService redisService, ChatMapper chatMapper) {
        this.redisService = redisService;
        this.chatMapper = chatMapper;
    }


    public void redisToDB() {
        String pattern = "roomId:*";
        Set<String> keys = redisService.keys(pattern);

        for (String key : keys) {
            Object message = redisService.get(key);
            if(message instanceof ChatDAO) {
                ChatDAO chatDAO = (ChatDAO) message;
                chatMapper.insertMessage(chatDAO);
                redisService.delete(key);
            }
        }
    }
}
