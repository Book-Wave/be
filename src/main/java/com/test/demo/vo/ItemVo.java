package com.test.demo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.test.demo.mapper.CategoryMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ItemVo: 상품 관련 정보를 담는 Value Object
 */
@Data
public class ItemVo {
    private int itemId; // 상품 ID
    private String itemName; // 상품 이름
    private int category; // 카테고리 ID
    private String categoryName;
    private int sellerId; // 판매자 ID
    private int buyerId; // 구매자 ID
    private int view; // 조회수
    private int trade; // 거래 상태
    private int status; // 상품 상태
    private int price; // 상품 가격
    private int oriPrice; // 원래 가격
    private String link; // 네이버 책 링크
    private String image; // 이미지 URL
    private String author; // 저자
    private String publisher; // 출판사
    private String description; // 책 설명


    private LocalDateTime regDate; // 등록 날짜
    private LocalDateTime modDate; // 수정 날짜

    @JsonIgnore
    public void setItemId(int itemId) {
        this.itemId = itemId; // setter는 내부에서만 사용
    }

    // 카테고리 이름 → 카테고리 ID 변환
    public int getCategoryId() {
        return CategoryMapper.getCategoryId(this.categoryName);
    }

    // 카테고리 ID → 카테고리 이름 반환
    public String getCategoryName() {
        return CategoryMapper.getCategoryName(this.category);
    }
}
