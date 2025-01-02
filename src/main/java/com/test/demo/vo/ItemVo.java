package com.test.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ItemVo {
    private int itemId;
    private String itemName;
    private String title;
    private int category;
    private String categoryName;
    private int sellerId;
    private int buyerId;
    private int view;
    private int trade;
    private int status;
    private int price;
    private int myPrice; // 이름 변경
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String description;
    private String note; // 새로운 필드 추가
    private String regDate; // String으로 변경
    private String modDate; // String으로 변경

//    // 생성자나 setter에서 LocalDateTime -> String 변환
//    public void setRegDate(LocalDateTime regDate) {
//        this.regDate = regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    }
//
//    public void setModDate(LocalDateTime modDate) {
//        this.modDate = modDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    }
}
