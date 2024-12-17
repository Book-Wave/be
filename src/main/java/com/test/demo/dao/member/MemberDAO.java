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

    public void register(MemberVO memberVO) { memberMapper.register(memberVO); }

    public boolean check_nickname(String nick_name) { return memberMapper.check_nickname(nick_name) == 0; }

    public int update_password(Long member_id, String encoded_password) {
        return memberMapper.update_password(member_id, encoded_password);
    }
}
