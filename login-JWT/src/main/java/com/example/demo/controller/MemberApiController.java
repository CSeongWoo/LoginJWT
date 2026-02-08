package com.example.demo.controller;
import com.example.demo.dto.JoinRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberJoinRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.entity.Member;
import com.example.demo.entity.Role;
import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
	
    /**
     * 로그인 API
     * 성공 시 JWT 토큰이 포함된 JSON 객체를 반환합니다.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        // 1. 서비스 로직을 통해 토큰 세트(Access + Refresh)를 받아옴 ⭐
        TokenResponse tokenResponse = memberService.login(request.getLoginId(), request.getPassword());

        // 2. 그대로 반환
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        TokenResponse tokenResponse = memberService.reissue(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequest request) { // JoinRequest DTO 필요
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .name(request.getName())
                .role(Role.USER)
                .build();

        memberService.join(member);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 권한 테스트용 API: 현재 로그인한 사용자의 ID를 반환
     * (SecurityContext에 저장된 인증 정보를 활용합니다)
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo() {
        String currentLoginId = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        return ResponseEntity.ok("현재 로그인된 사용자는: " + currentLoginId + " 입니다.");
    }
}