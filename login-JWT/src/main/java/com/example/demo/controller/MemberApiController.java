package com.example.demo.controller;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberJoinRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.entity.Member;
import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // 1. 서비스 로직을 통해 아이디/비밀번호 검증
        Member member = memberService.login(request.getLoginId(), request.getPassword());

        // 2. 수정된 TokenResponse 구조 적용 ⭐
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("temp-jwt-token") // 이제 이 부분을 JwtTokenProvider가 채웁니다.
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(tokenResponse);
    }

//    // 회원가입 페이지 열기 (테스트용)
//    @GetMapping("/signup")
//    public String signupPage() {
//        return "signup";
//    }
}