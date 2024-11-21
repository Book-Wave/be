package com.test.demo.dao.member;

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

    public void save(MemberVO memberVO) {
        memberMapper.save(memberVO);
    }

    public MemberVO find_by_id_provider(String oauth_provider, String oauth_id) { return memberMapper.find_by_id_provider(oauth_provider, oauth_id); }

    public MemberVO find_by_email(String email) { return memberMapper.find_by_email(email); }
}
