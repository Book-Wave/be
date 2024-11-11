package com.test.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
public class TestController {
    @GetMapping("/hello")
    public ResponseEntity<Object> testApi() {
        String result = "API 통신에 성공했습니다.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/actions")
    public ResponseEntity<Object> testAction() {
        String result = "Actions 진입에 성공했습니다.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
