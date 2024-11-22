package com.test.demo.mapper;

import com.test.demo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    void insert_member(MemberVO memberVO);
}
