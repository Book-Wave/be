package com.test.demo.service.member;


import com.test.demo.config.jwt.JwtTokenProvider;
import com.test.demo.dao.member.MemberDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.MemberVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDAO memberDAO;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private static final String pw_pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
    // 알파벳 + 숫자 + 특수기호로 이루어진 8자리 이상

    public MemberVO check_member(MemberVO memberVO, String oauth_provider) {
        return memberDAO.find_by_id_provider(oauth_provider, memberVO.getOauth_id());
    }

    public Map<String, String> social_register(Map<String, Object> requestData, HttpSession session) {
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
        String token = jwtTokenProvider.create_token(memberVO.getEmail(), memberVO.getMember_id(), memberVO.getNick_name());
        String refresh_token = jwtTokenProvider.create_refresh_token(memberVO.getEmail());

        redisService.set("RT:" + memberVO.getEmail(), refresh_token, 30 * 24 * 60);

        session.removeAttribute("oauth_provider");
        session.removeAttribute("oauth_id");
        session.removeAttribute("name");
        session.removeAttribute("email");

        Map<String, String> response = new HashMap<>();
        response.put("access_token", token);
        response.put("refresh_token", refresh_token);
        return response;
    }

    public String login_callback(String oauth_provider, String code, String state, HttpSession session, HttpServletResponse res) {
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
                    String token = jwtTokenProvider.create_token(existing.getEmail(), existing.getMember_id(), existing.getNick_name());
                    String refresh_token = jwtTokenProvider.create_refresh_token(existing.getEmail());
                    redisService.set("RT:" + existing.getEmail(), refresh_token, 30 * 24 * 60);
                    if (oauth_provider.equals("kakao")) {
                        redisService.set("kakao:" + existing.getEmail(), "kakao", 30 * 24 * 60);
                    }
                    jwtTokenProvider.setRefreshTokenInCookie(res, refresh_token);
                    return token;
                } else {
                    session.setAttribute("oauth_provider", memberVO.getOauth_provider());
                    session.setAttribute("oauth_id", memberVO.getOauth_id());
                    session.setAttribute("name", memberVO.getName());
                    session.setAttribute("email", memberVO.getEmail());
                    return "new_user";
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

    public void register(MemberVO memberVO) {
        if (memberDAO.find_by_email(memberVO.getEmail()) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (!isValidPassword(memberVO.getPassword())) {
            throw new IllegalArgumentException("올바르지 않은 비밀번호입니다.");
        }

        String encoded_password = passwordEncoder.encode(memberVO.getPassword());
        memberVO.setPassword(encoded_password);
        memberDAO.register(memberVO);
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(pw_pattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public String login(Map<String, String> loginRequest, HttpServletResponse res) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        MemberVO memberVO = memberDAO.find_by_email(email);
        if (memberVO == null) {
            throw new IllegalArgumentException("Invalid email.");
        }
        if (!passwordEncoder.matches(password, memberVO.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }

        String access_token = jwtTokenProvider.create_token(email, memberVO.getMember_id(), memberVO.getNick_name());
        String refresh_token = jwtTokenProvider.create_refresh_token(email);
        redisService.set("RT:" + email, refresh_token, 30 * 24 * 60);
        jwtTokenProvider.setRefreshTokenInCookie(res, refresh_token);
        return access_token;
    }

    public String refresh_access_token(String refresh_token, HttpServletResponse res) {
        if (!jwtTokenProvider.validate_refresh_token(refresh_token)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refresh_token);
        String stored_refresh_token = (String) redisService.get("RT:" + email);
        if (stored_refresh_token == null || !stored_refresh_token.equals(refresh_token)) {
            throw new IllegalArgumentException("Refresh token does not match.");
        }

        MemberVO memberVO = memberDAO.find_by_email(email);
        if (memberVO == null) {
            throw new IllegalArgumentException("Member does not exist.");
        }
        String new_access_token = jwtTokenProvider.create_token(email, memberVO.getMember_id(), memberVO.getNick_name());
        jwtTokenProvider.deleteRefreshTokenInCookie(res);
        redisService.delete("RT:" + email);
        return new_access_token;
    }

    public boolean check_nickname(String nick_name) {
        return memberDAO.check_nickname(nick_name);
    }

    public void reset_password(Map<String, Object> requestData) {
        String email = (String) requestData.get("email");
        String password = (String) requestData.get("password");
        String confirm_password = (String) requestData.get("confirm_password");

        if (!password.equals(confirm_password)) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 재확인이 일치하지 않습니다.");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("올바르지 않은 비밀번호입니다.");
        }
        MemberVO memberVO = memberDAO.find_by_email(email);
        if (memberVO == null || memberVO.getPassword() == null) {
            throw new IllegalArgumentException("가입되지 않은 이메일입니다.");
        }
        String encoded_password = passwordEncoder.encode(password);
        int row_update = memberDAO.update_password(memberVO.getMember_id(), encoded_password);
        if (row_update != 1) {
            throw new RuntimeException("비밀번호 재설정에 실패했습니다.");
        }
    }

    public String logout(HttpServletRequest req, HttpServletResponse res) {
        try {
            String access_token = jwtTokenProvider.resolveToken(req);
            if (!jwtTokenProvider.validate_token(access_token)) {
                throw new IllegalArgumentException("Invalid or expired token.");
            }
            String email = jwtTokenProvider.getEmailFromToken(access_token);
            redisService.delete("RT:" + email);
            jwtTokenProvider.deleteRefreshTokenInCookie(res);
            if (redisService.exists("kakao:" + email)) {
                return kakaoOAuthService.kakao_logout();
            }
            return "success";
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to process logout: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during logout.", e);
        }
    }
}
