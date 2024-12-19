use bw;

CREATE TABLE chat (
                      message_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 메시지 고유 ID
                      room_id VARCHAR(255) NOT NULL,               -- 방 ID (sender-receiver 형식)
                      sender VARCHAR(255) NOT NULL,                -- 메시지 보낸 사람
                      receiver VARCHAR(255) NOT NULL,              -- 메시지 받는 사람
                      message TEXT NOT NULL,                       -- 메시지 내용
                      time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 메시지 전송 시간
                      is_read BOOLEAN DEFAULT FALSE               -- 메시지 읽음 상태
);



CREATE TABLE chatroom (
                          room_id VARCHAR(255) PRIMARY KEY,            -- 방 ID (sender-receiver 형식)
                          user_one VARCHAR(255) NOT NULL,              -- 채팅 참여자 1
                          user_two VARCHAR(255) NOT NULL,              -- 채팅 참여자 2
                          created_time DATETIME DEFAULT CURRENT_TIMESTAMP -- 방 생성 시간
);

TRUNCATE TABLE chat;