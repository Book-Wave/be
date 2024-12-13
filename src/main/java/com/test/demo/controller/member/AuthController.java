package com.test.demo.controller.member;

import com.test.demo.config.jwt.JwtTokenProvider;
import com.test.demo.service.member.KakaoOAuthService;
import com.test.demo.service.member.MailService;
import com.test.demo.service.member.MemberService;
import com.test.demo.service.member.NaverOAuthService;
import com.test.demo.vo.MemberVO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/book/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final MemberService memberService;
    private final MailService mailService;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("{provider}/login")
    public ResponseEntity<String> login_oauth(@PathVariable("provider") String provider) {
        String login_url = null;
        if (provider.equals("kakao")) {
            login_url = kakaoOAuthService.kakao_login();
        } else if (provider.equals("naver")) {
            login_url = naverOAuthService.naver_login();
        }
        return ResponseEntity.status(302).location(URI.create(login_url)).build(); // 새 URL은 임시적이며, 클라이언트가 기억하지 않아야 함.
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> login_request, HttpServletResponse res) {
        try {
            String access_token = memberService.login(login_request, res);
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token).body("success");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("fail: " + e.getMessage());
        }
    }

    @GetMapping("/{provider}/callback")
    public ResponseEntity<?> oauth_callback(@PathVariable("provider") String provider,
                                            @RequestParam("code") String code,
                                            @RequestParam(name = "state", required = false) String state,
                                            HttpSession session) {
        try {
            Map<String, Object> response = memberService.login_callback(provider, code, state, session);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(provider + " login failed."); // 서버 오류
        }
    }


    @PostMapping("/social/new")
    public ResponseEntity<?> social_regiser(@RequestBody Map<String, Object> request_data, HttpSession session) {
        try {
            Map<String, String> response = memberService.social_register(request_data, session);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // 클라이언트가 잘못된 데이터를 서버로 전송하거나, 요청 형식이 규칙에 맞지 않는 경우
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Social register failed.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MemberVO memberVO) {
        try {
            memberService.register(memberVO);
            return ResponseEntity.status(201).body("success"); // 201 Created: 요청이 성공적으로 처리되어서 리소스가 만들어짐
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/email_send")
    public ResponseEntity<String> send_mail(@RequestBody Map<String, String> email_data) {
        String email = email_data.get("email");
        try {
            mailService.send_email(email);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("fail");
        }
    }

    @PostMapping("/email_verify")
    public ResponseEntity<String> verify_code(@RequestBody Map<String, Object> request_data) {
        boolean flag = mailService.verify_code((String) request_data.get("email"), Integer.parseInt((String) request_data.get("code")));
        return flag ? ResponseEntity.ok("success") : ResponseEntity.status(400).body("fail");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh_token(HttpServletRequest req, HttpServletResponse res) {
        String refresh_token = jwtTokenProvider.getRefreshTokenFromCookie(req);
        try {
            String new_access_token = memberService.refresh_access_token(refresh_token, res);
            return ResponseEntity.ok(Map.of("token", new_access_token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("fail");
        }
    }

    @GetMapping("/nickname/check/{nickname}")
    public ResponseEntity<Boolean> check_nickname(@PathVariable("nickname") String nick_name) {
        boolean is_duplicated = memberService.check_nickname(nick_name);
        return ResponseEntity.ok(is_duplicated);
    }
}