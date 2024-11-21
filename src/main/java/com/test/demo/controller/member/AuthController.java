package com.test.demo.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.demo.config.JwtTokenProvider;
import com.test.demo.dao.member.MemberDAO;
import com.test.demo.mapper.MemberMapper;
import com.test.demo.service.member.KakaoOAuthService;
import com.test.demo.service.member.MemberService;
import com.test.demo.service.member.NaverOAuthService;
import com.test.demo.vo.MemberVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/book/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final MemberService memberService;
//    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final MemberDAO memberDAO;

    @GetMapping("kakao/login")
    public ResponseEntity<String> kakao_login() {
        String login_url = kakaoOAuthService.kakao_login();
        System.out.println("카카오 로그인 진입");
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(login_url)).build();
    }

    @GetMapping("naver/login")
    public ResponseEntity<String> naver_login() {
        String login_url = naverOAuthService.naver_login();
        System.out.println("네이버 로그인 진입");
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(login_url)).build();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakao_callback(@RequestParam("code") String code, HttpSession session) {
        logger.info("Received Kakao login callback with code: {}", code);
        try {
            MemberVO memberVO = kakaoOAuthService.get_kakao_user_info(code);  // KakaoOAuthService 호출
            if (memberVO != null) {
                logger.info("Kakao user info retrieved: {}", memberVO);
                MemberVO member = memberService.check_kakao(memberVO);
                if (member != null) {
                    String token = memberService.create_token(member.getEmail(), member.getMember_id());
                    return ResponseEntity.ok().body(Map.of("token", token));
                } else {
                    logger.info("Kakao user info does not exist");
                    session.setAttribute("oauth_provider", memberVO.getOauth_provider());
                    session.setAttribute("oauth_id", memberVO.getOauth_id());
                    session.setAttribute("name", memberVO.getName());
                    session.setAttribute("email", memberVO.getEmail());
                    return ResponseEntity.ok().body(Map.of("new_user", true));
                }
            } else {
                logger.error("Failed to retrieve Kakao user info.");
                return ResponseEntity.status(500).body("Failed to retrieve user info from Kakao.");
            }
        } catch (Exception e) {
            logger.error("Error during Kakao login process", e);
            return ResponseEntity.status(500).body("Kakao login failed.");
        }
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<?> naver_callback(@RequestParam("code") String code, @RequestParam("state") String state, HttpSession session) {
        logger.info("Received Naver login callback with code: {}, state: {}", code, state);
        try {
            MemberVO memberVO = naverOAuthService.get_naver_user_info(code, state);  // NaverOAuthService 호출
            if (memberVO != null) {
                logger.info("Naver user info retrieved: {}", memberVO);
                MemberVO member = memberService.check_naver(memberVO);
                if (member != null) {
                    String token = memberService.create_token(member.getEmail(), member.getMember_id());
                    return ResponseEntity.ok().body(Map.of("token", token));
                } else {
                    logger.info("Naver user info does not exist");
                    session.setAttribute("oauth_provider", memberVO.getOauth_provider());
                    session.setAttribute("oauth_id", memberVO.getOauth_id());
                    session.setAttribute("name", memberVO.getName());
                    session.setAttribute("email", memberVO.getEmail());
                    return ResponseEntity.ok().body(Map.of("new_user", true));
                }
            } else {
                logger.error("Failed to retrieve Naver user info.");
                return ResponseEntity.status(500).body("Failed to retrieve user info from Naver.");
            }
        } catch (Exception e) {
            logger.error("Error during Naver login process", e);
            return ResponseEntity.status(500).body("Naver login failed.");
        }
    }

    @PostMapping("/social/new")
    public ResponseEntity<?> kakao_regiser(@RequestBody Map<String, Object> requestData,
                                           HttpSession session) {
        String nick_name = (String) requestData.get("nickname");
        String birth_date = (String) requestData.get("birthdate");
        int gender = Integer.parseInt((String) requestData.get("gender"));
        try {
            String oauth_provider = (String) session.getAttribute("oauth_provider");
            String oauth_id = (String) session.getAttribute("oauth_id");
            String name = (String) session.getAttribute("name");
            String email = (String) session.getAttribute("email");

            if (oauth_provider == null || oauth_id == null) {
                return ResponseEntity.status(400).body("Invalid session. Please retry login.");
            }
            MemberVO memberVO = new MemberVO(oauth_provider, oauth_id, name, email, nick_name, birth_date, gender);

            memberService.save(memberVO);
            String token = memberService.create_token(memberVO.getEmail(), memberVO.getMember_id());
            logger.info("Kakao user info retrieved: {}", token);

            session.removeAttribute("oauth_provider");
            session.removeAttribute("oauth_id");
            session.removeAttribute("name");
            session.removeAttribute("email");

            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (Exception e) {
            logger.error("Error during Kakao register process", e);
            return ResponseEntity.status(500).body("Kakao register failed.");
        }
    }


}
