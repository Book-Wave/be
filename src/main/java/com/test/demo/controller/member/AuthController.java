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
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;

    @GetMapping("{provider}/login")
    public ResponseEntity<String> login(@PathVariable("provider") String provider) {
        String login_url = null;
        if (provider.equals("kakao")) {
            login_url = kakaoOAuthService.kakao_login();
        } else if (provider.equals("naver")) {
            login_url = naverOAuthService.naver_login();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(login_url)).build();
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
            logger.error("Error during {} login process", provider, e);
            return ResponseEntity.status(500).body(provider + " login failed.");
        }
    }


    @PostMapping("/social/new")
    public ResponseEntity<?> social_regiser(@RequestBody Map<String, Object> requestData, HttpSession session) {
        try {
            String token = memberService.social_register(requestData, session);
            logger.info("Social user info retrieved: {}", token);
            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error during Social register process", e);
            return ResponseEntity.status(500).body("Social register failed.");
        }
    }


}
