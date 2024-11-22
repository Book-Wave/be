package com.test.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {
    private int memberId;
    private String id;
    private String pw;
    private String kakao;
    private String naver;
    private String email;
    private String name;
    private int hertz;
}
