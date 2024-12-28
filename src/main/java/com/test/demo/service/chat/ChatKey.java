package com.test.demo.service.chat;

public class ChatKey {
    public static String generateSortedKey(String prefix, String userOne, String userTwo) {
        String sortedKey = (userOne.compareTo(userTwo) <= 0) ? userOne + "-" + userTwo : userTwo + "-" + userOne;
        return prefix + ":" + sortedKey;
    }

    public String[] splitRoomId(String roomId) {
        if (roomId == null || !roomId.contains("-")) {
            throw new IllegalArgumentException("유효하지 않은 roomId 형식입니다: " + roomId);
        }
        return roomId.split("-", 2); // "-"로 구분하여 최대 2개의 요소로 분리
    }

}
