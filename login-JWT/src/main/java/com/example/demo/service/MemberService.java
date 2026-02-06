package com.example.demo.service;

import com.example.demo.dto.MemberJoinRequest;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 서비스 컴포넌트
@Service
@Transactional(readOnly = true) // 트랜잭션 설정
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; 

    /**
     * 회원가입
     */
    @Transactional
    public Long join(MemberJoinRequest request) {
        // 1. 아이디 중복 체크
        memberRepository.findByLoginId(request.getLoginId())
                .ifPresent(m -> { throw new IllegalStateException("이미 존재하는 아이디입니다."); });

        // 2. 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        return memberRepository.save(request.toEntity(encodedPassword)).getId();
    }

    /**
     * 로그인 검증 (JWT 발급 전 단계)
     */
    public Member login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }
}