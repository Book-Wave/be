package com.test.demo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemVo {
    private int itemId;         // item_id: 상품 ID

    private String itemName;    // item_name: 상품 이름
    private int category;       // category: 카테고리
    private int sellerId;       // seller_id: 판매자 ID
    private int buyerId;        // buyer_id: 구매자 ID
    private int view;           // view: 조회수
    private int trade;          // trade: 거래 상태
    private int status;         // status: 상품 상태
    private int price;          // price: 상품 가격
    private int oriPrice;       // ori_price: 원래 가격

    private LocalDate regDate;  // reg_date: 등록 날짜
    private LocalDate modDate;  // mod_date: 수정 날짜

    // itemId를 요청 시 무시하도록 설정
    @JsonIgnore
    public void setItemId(int itemId) {
        this.itemId = itemId; // setter는 내부에서만 사용 가능
    }
}
