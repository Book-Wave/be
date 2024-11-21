package com.test.demo.service.member;


import com.test.demo.config.JwtTokenProvider;
import com.test.demo.dao.member.MemberDAO;
import com.test.demo.vo.MemberVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDAO memberDAO;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;

    public MemberVO check_member(MemberVO memberVO, String oauth_provider) {
        return memberDAO.find_by_id_provider(oauth_provider, memberVO.getOauth_id());
    }

    public String social_register(Map<String, Object> requestData, HttpSession session) {
        String nick_name = (String) requestData.get("nickname");
        String birth_date = (String) requestData.get("birthdate");
        int gender = Integer.parseInt((String) requestData.get("gender"));

        String oauth_provider = (String) session.getAttribute("oauth_provider");
        String oauth_id = (String) session.getAttribute("oauth_id");
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");

        if (oauth_provider == null || oauth_id == null) {
            throw new IllegalArgumentException("Invalid session. Please retry login.");
        }
        MemberVO memberVO = new MemberVO(oauth_provider, oauth_id, name, email, nick_name, birth_date, gender);
        save(memberVO);
        String token = create_token(memberVO.getEmail(), memberVO.getMember_id());

        session.removeAttribute("oauth_provider");
        session.removeAttribute("oauth_id");
        session.removeAttribute("name");
        session.removeAttribute("email");

        return token;
    }

    public Map<String, Object> login_callback(String oauth_provider, String code, String state, HttpSession session) {
        try {
            MemberVO memberVO = null;
            if ("kakao".equals(oauth_provider)) {
                memberVO = kakaoOAuthService.get_kakao_user_info(code);
            } else if ("naver".equals(oauth_provider)) {
                memberVO = naverOAuthService.get_naver_user_info(code, state);
            }

            if (memberVO != null) {
                MemberVO existing = check_member(memberVO, oauth_provider);
                if (existing != null) {
                    String token = create_token(existing.getEmail(), existing.getMember_id());
                    return Map.of("token", token);
                } else {
                    session.setAttribute("oauth_provider", memberVO.getOauth_provider());
                    session.setAttribute("oauth_id", memberVO.getOauth_id());
                    session.setAttribute("name", memberVO.getName());
                    session.setAttribute("email", memberVO.getEmail());
                    return Map.of("new_user", true);
                }
            } else {
                throw new Exception("Failed to retrieve user info.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during OAuth login callback process", e);
        }
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
