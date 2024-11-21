package com.test.demo.service.member;


import com.test.demo.config.JwtTokenProvider;
import com.test.demo.dao.member.MemberDAO;
import com.test.demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDAO memberDAO;


    public MemberVO check_kakao(MemberVO memberVO) {
        MemberVO existingMember = memberDAO.find_by_id_provider("kakao", memberVO.getOauth_id());
        return existingMember;
    }

    public MemberVO check_naver(MemberVO memberVO) {
        MemberVO existingMember = memberDAO.find_by_id_provider("naver", memberVO.getOauth_id());
        return existingMember;
    }

    @Transactional
    public void save(MemberVO memberVO) {
        memberDAO.save(memberVO);
    }

    public MemberVO get_by_email(String email) {
        return memberDAO.find_by_email(email);
    }

    public String create_token(String email, Long member_id) {
        return jwtTokenProvider.create_token(email, member_id);
    }
}
