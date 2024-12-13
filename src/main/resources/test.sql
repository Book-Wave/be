use bw;

CREATE TABLE chat_rooms (
                            room_id VARCHAR(255) PRIMARY KEY,   -- 고유 식별자
                            room_name VARCHAR(255),            -- 방 이름
                            userone VARCHAR(255) NULL,         -- 첫 번째 사용자 (nullable)
                            usertwo VARCHAR(255) NULL,         -- 두 번째 사용자 (nullable)
                            room_date DATE NOT NULL-- 방 생성 날짜
);

CREATE TABLE chat (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 메시지 고유 ID
                      type VARCHAR(50) NOT NULL,             -- 메시지 타입 (예: TEXT, IMAGE)
                      room_id VARCHAR(255) NOT NULL,         -- 관련된 채팅방 ID
                      sender VARCHAR(255) NOT NULL,          -- 메시지 전송자
                      message TEXT NOT NULL,                 -- 메시지 내용
                      time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 메시지 전송 시간
                      FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id) -- 참조 무결성
);