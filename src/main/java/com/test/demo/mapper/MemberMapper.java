package com.test.demo.mapper;

import com.test.demo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    void save(MemberVO memberVO);

    MemberVO find_by_id_provider(@Param("oauth_provider") String oauth_provider, @Param("oauth_id") String oauth_id);

    MemberVO find_by_email(String email);

    void register(MemberVO memberVO);

    int check_nickname(String nick_name);
}
