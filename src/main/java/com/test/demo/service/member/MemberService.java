package com.test.demo.service.member;


import com.test.demo.config.jwt.JwtTokenProvider;
import com.test.demo.dao.member.MemberDAO;
import com.test.demo.service.RedisService;
import com.test.demo.vo.MemberVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDAO memberDAO;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private static final String pw_pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

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
        String token = jwtTokenProvider.create_token(memberVO.getEmail(), memberVO.getMember_id());
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
                    String token = jwtTokenProvider.create_token(existing.getEmail(), existing.getMember_id());
                    String refresh_token = jwtTokenProvider.create_refresh_token(existing.getEmail());
                    redisService.set("RT:" + existing.getEmail(), refresh_token, 30 * 24 * 60);
                    Map<String, Object> response = new HashMap<>();
                    response.put("access_token", token);
                    response.put("refresh_token", refresh_token);
                    return response;
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

    public void register(MemberVO memberVO) {
        if (memberDAO.find_by_email(memberVO.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exist.");
        }
        if (!isValidPassword(memberVO.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
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

    public MemberVO get_by_email(String email) {
        log.info("get by email : {}", email);
        return memberDAO.find_by_email(email);
    }

    public Map<String, String> login(Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        Map<String, String> map = new HashMap<>();

        MemberVO memberVO = memberDAO.find_by_email(email);
        if (memberVO == null) {
            throw new IllegalArgumentException("Invalid email.");
        }
        if (!passwordEncoder.matches(password, memberVO.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }
        String access_token = jwtTokenProvider.create_token(email, memberVO.getMember_id());
        String refresh_token = jwtTokenProvider.create_refresh_token(email);
        redisService.set("RT:" + email, refresh_token, 30 * 24 * 60);
        map.put("access_token", access_token);
        map.put("refresh_token", refresh_token);
        return map;
    }

    public String refresh_access_token(String authorization) {
        String refresh_token = authorization.substring(7);
        log.info("refresh access token : {}", refresh_token);
        if (!jwtTokenProvider.validate_refresh_token(refresh_token)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refresh_token);
        log.info("refresh email : {}", email);
        String stored_refresh_token = (String) redisService.get("RT:" + email);
        log.info("refresh stored refresh_token : {}", stored_refresh_token);
        if (stored_refresh_token == null || !stored_refresh_token.equals(refresh_token)) {
            throw new IllegalArgumentException("Refresh token does not match.");
        }

        MemberVO memberVO = memberDAO.find_by_email(email);
        if (memberVO == null) {
            throw new IllegalArgumentException("Member does not exist.");
        }
        return jwtTokenProvider.create_token(email, memberVO.getMember_id());
    }
}
