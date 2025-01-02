package com.test.demo.vo;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class PostVo {
    private int postId;        // 게시물 ID
    private String postTitle;  // 게시물 제목
    private int itemId;        // 상품 ID (item 테이블 참조)
    private int sellerId;      // 판매자 ID
    private int price;         // 가격
    private String description; // 게시물 설명
    private String notes;      // 참고 사항
    private LocalDateTime regDate; // 등록 날짜
    private LocalDateTime modDate; // 수정 날짜
}
