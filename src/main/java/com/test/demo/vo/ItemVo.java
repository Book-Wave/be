package com.test.demo.vo;

import lombok.Data;

@Data // Lombok을 사용하여 getter, setter, toString 등을 자동 생성
public class ItemVo {
    private int id;          // 상품 ID
    private String name;     // 상품 이름
    private String publisher;// 출판사
    private String author;   // 저자
    private int price;       // 가격
    private int views;       // 조회수
}
