package com.test.demo.controller;

import com.test.demo.service.MemberService;
import com.test.demo.vo.MemberVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("book/auth")
public class MemberController {
    @Autowired
    private MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Value("${kakaoAPI.tokenUrl}") String token_url;
    @Value("${kakaoAPI.clientId}") String client_id;
    @Value("${kakaoAPI.redirectUrl}") String redirect_url;
    @Value("${kakaoAPI.logoutUrl}") String logout_url;

    @PostMapping("/register")
    public ResponseEntity<Object> service_register() {
        String result = "서비스 내부에 회원가입 프로세스 작동.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register/email")
    public ResponseEntity<Object> service_register_email() {
        String result = "서비스 내부에 회원가입 시 이메일 처리 프로세스 작동.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/register/addmore")
    public ResponseEntity<Object> service_register_addmore() {
        String result = "소셜 간편 회원가입 이후 추가 정보를 받는 프로세스 작동.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<String> kakao_login() {
//        session.invalidate();
        String kakao_login_url = "https://kauth.kakao.com/oauth/authorize?client_id=" + client_id + "&redirect_uri=" + redirect_url + "&response_type=code";
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", kakao_login_url)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> kakao_login_callback(@RequestParam("code") String code){
        String kakao_token = memberService.get_kakao_token(code);
        Map<String, Object> kakao_info_map = memberService.get_kakao_info(kakao_token);

        return ResponseEntity.ok("member_info: " + kakao_info_map);

    }


}
