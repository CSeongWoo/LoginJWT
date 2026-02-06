package com.example.demo.service;

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
     * 회원가입 로직
     */
    @Transactional
    public Long join(Member member) {
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        
        // 엔티티를 직접 수정하기보다 Builder나 별도 메서드를 권장하지만, 일단 기본 필드 세팅
        // 실제 구현 시에는 DTO를 받아 Member를 생성하는 로직이 들어갑니다.
        
        return memberRepository.save(member).getId();
    }
}