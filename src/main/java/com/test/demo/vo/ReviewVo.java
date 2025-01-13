package com.test.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReviewVo {
    private int itemId;
    private int sellerId;
    private int buyerId;
    private Boolean one;
    private Boolean two;
    private Boolean three;
    private Boolean four;
    private Boolean five;
}