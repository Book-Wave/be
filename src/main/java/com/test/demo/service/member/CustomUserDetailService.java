package com.test.demo.service.member;

import com.test.demo.dao.member.MemberDAO;
import com.test.demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberDAO memberDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberVO memberVO = memberDAO.find_by_email(email);
        if (memberVO == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return User.builder()
                .username(memberVO.getEmail()) // 주로 email을 username으로 사용
                .password("") // JWT 인증에서는 패스워드 검증이 없으므로 빈 문자열 사용
//                .authorities("USER") // 기본 권한 부여
                .build();
    }
}
