drop table chat_rooms;


CREATE TABLE chat_rooms (
                            room_id VARCHAR(36) PRIMARY KEY,  -- UUID 형식의 방 ID
                            room_name VARCHAR(255) NOT NULL,   -- 방 이름
                            room_date DATE NOT NULL             -- 방 생성 날짜
);

use bw;

CREATE TABLE chat (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 메시지 ID, 기본 키 (자동 증가)
                      type ENUM('ENTER', 'TALK') NOT NULL,    -- 메시지 유형, ENUM으로 설정
                      room_id VARCHAR(255) NOT NULL,          -- 채팅방 ID
                      sender VARCHAR(255) NOT NULL,           -- 발신자
                      message TEXT NOT NULL,                   -- 메시지 내용
                      time DATETIME NOT NULL                   -- 메시지 전송 시간
);