package com.test.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("book/auth")
public class MemberController {

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

    @PostMapping("/login")
    public ResponseEntity<Object> service_login() {
        String result = "서비스 내부 로그인 프로세스 작동.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
