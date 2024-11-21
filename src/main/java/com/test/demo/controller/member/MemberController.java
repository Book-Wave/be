package com.test.demo.controller.member;

import com.test.demo.service.member.MemberService;
import com.test.demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("book/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        // 인증된 사용자의 이메일로 사용자 정보 조회
        String email = user.getUsername();
        MemberVO memberVO = memberService.get_by_email(email);
        return ResponseEntity.ok(memberVO);
    }
}
