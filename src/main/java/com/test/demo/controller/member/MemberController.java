package com.test.demo.controller.member;

import com.test.demo.service.member.MemberService;
import com.test.demo.vo.MemberVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("book/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getMemberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 이메일로 사용자 정보 조회
        String email = authentication.getName();
        String nickname = (String) authentication.getDetails();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("nickname", nickname);
        return ResponseEntity.ok(map);
    }

}
