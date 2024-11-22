package com.test.demo.dao;

import com.test.demo.mapper.MemberMapper;
import com.test.demo.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDAO {
    private final MemberMapper memberMapper;

    @Autowired
    public MemberDAO(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public void save_member(MemberVO memberVO) {
        memberMapper.insert_member(memberVO);
    }
}
