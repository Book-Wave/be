package com.test.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {
    private Long member_id;
    private String oauth_provider; // kakao or naver
    private String oauth_id;
    private String name;
    private String email;
    private String nick_name;
    private String birth_date;
    private int gender; // 0 -> woman , 1 - > man
    private int hertz;

    public MemberVO(String oauth_provider, String oauth_id, String name, String email, String nick_name, String birth_date, int gender) {
        this.oauth_provider = oauth_provider;
        this.oauth_id = oauth_id;
        this.name = name;
        this.email = email;
        this.nick_name = nick_name;
        this.birth_date = birth_date;
        this.gender = gender;
    }
}

