package com.test.demo.controller;


import com.test.demo.RedisTest;
import com.test.demo.vo.chat.ChatVO;
import com.test.demo.vo.chat.ChatRoomVO;
import com.test.demo.service.chat.ChatRoomService;
import com.test.demo.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/book/chat")
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatRoomService chatRoomService;

    private final ChatService chatService;

    private final RedisTest redisTest;



/*
* 기본적으로 구현하려는 기능은 1:1 채팅 -> 사용자가 같은 채널을 구독하고, 같은 채널에 발행을 해야한다.
* 기본적으로 사용자는 만들어지는 채팅창을 구독해야한다 -> 그리고 발행하는 식으로 운영
* 기본적으로 prefix가 서버를 거칠 때 발행 /pub/roomid 로 발행이 되고, 보내는 작업을 서버에서 /sub/roomid에 구독한 사람에게 모두 보낸다.
*
* */

    //  일반url
    // 모든 채팅방 목록 반환
    @GetMapping(value = "/rooms")
    public ResponseEntity<List<ChatRoomVO>> room() {
        try {
            List<ChatRoomVO> rooms = chatRoomService.findAllRoom();
            log.info(rooms.toString());
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("채팅방 목록 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // 채팅방 생성
    // 일반 url
    @PostMapping("/rooms")
    public ResponseEntity<String> createRoom(@RequestBody Map<String, String> requestBody) {
        try {
            String userone = requestBody.get("userone");
            String usertwo = requestBody.get("usertwo");
            String roomname = requestBody.get("roomName");
//            if (name == null || name.isEmpty()) {
//                throw new IllegalArgumentException("name은 필수 값입니다.");
//            }
            chatRoomService.createRoom(roomname, userone, usertwo);
            return ResponseEntity.ok("sucess");
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("fail "+e.getMessage());
        }
    }



    // 특정 채팅방 조회
    // 일반 url
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomVO> roomInfo(@PathVariable String roomId) {
        try {
            ChatRoomVO room = chatRoomService.findById(roomId);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            log.error("채팅방 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /*
    * 1. 프론트에서 채널 구독
    * 2. 프론트에서 /pub/roomid로 메세지 발간
    * 3. 서버에서 message 받고 db 저장 및 /sub/roomid로 roomid에 구독된 사용자들에게 메세지 보내기
    * 애초에 프론트에서 메세지를 보낼 때 채팅방에 connect -> 그 roomid를 가지고 메세지를 보낸다.
    * */

    //   메세지 방법 1. @sendto -> return message, 2번 convert and send 자체가 메세지 전송을 해준다

/* publish
*  destination /config의 prefix/message 등 publish당시 적을 거, 이것은 채팅방과 같은 식별자라기보다는 publish 자체에 대한
*  고유 명칭을 달아야한다
*  @MessageMapping(/publish고유식별자)
*
*  subscribe
*  /config의 broker/고유식별자 (ex) 방 번호 등)
*  @SendTo("/broker/고유식별자")
*  sendto는 url 작성 시 브로커를 붙여야 한다.
* */
//  @SendTo("/sub/{roomId}")
//    이 서비스에서 지금 발간된 메세지를 일단 저장
//return하거나 convertandsend 메서드를 통해 /sub/roomid에 구독된 사용자들에게 메세지 전송
    @MessageMapping("/message")
    public void handleMessage(@Payload ChatVO message) {
        try {
            log.info("message: {}", message);
            chatService.saveMessage(message.getRoomId(), message.getSender(), message.getMessage(), message.getType());
            simpMessagingTemplate.convertAndSend("/sub/" + message.getRoomId(), message);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    // 특정 채팅방의 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatVO>> getMessagesByRoomId(@PathVariable String roomId, @RequestParam String sender) {
        try {
            System.out.println("메세지 조회");
            log.info("sender = {}",sender);
            List<ChatVO> messages = chatService.findMessagesByRoomId(roomId, sender);
            log.info(messages.toString());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("메시지 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 메시지 삭제
    @DeleteMapping("/rooms/{roomId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String roomId, @PathVariable Long messageId) {
        try {
            chatService.deleteMessage(roomId,messageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("메시지 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test")
    public void test() {
        redisTest.testRedis();

    }

}
