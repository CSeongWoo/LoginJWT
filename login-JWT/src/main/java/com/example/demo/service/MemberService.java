package com.example.demo.service;

import com.example.demo.dto.MemberJoinRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.entity.Member;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.security.JwtTokenProvider;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder; 

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        // 1. 중복 회원 검증 (아이디 중복 체크)
        validateDuplicateMember(member);

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.updatePassword(encodedPassword); // Member 엔티티에 암호화된 비번 세팅

        // 3. 권한 설정 (기본 USER 권한)
        if (member.getRole() == null) {
            member.updateRole(Role.USER);
        }

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 아이디입니다.");
                });
    }

    /**
     * 로그인 검증
     */
    @Transactional
    public TokenResponse login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        TokenResponse tokenResponse = jwtTokenProvider.createTokenSet(member);

        // DB에 Refresh Token 저장 (기존 꺼 있으면 업데이트)
        RefreshToken refreshToken = new RefreshToken(member.getLoginId(), tokenResponse.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return tokenResponse;
    }

    // 2. 토큰 재발급 로직
    @Transactional
    public TokenResponse reissue(String oldRefreshToken) {
        // 1단계: Refresh Token 자체의 유효성 검사
        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2단계: 토큰에서 사용자 정보 추출
        String loginId = jwtTokenProvider.getSubject(oldRefreshToken);
        // 로그 추가: 실제로 어떤 값으로 찾으려 하는지 확인
        System.out.println("디버깅 - 추출된 loginId: " + loginId);

        RefreshToken savedToken = refreshTokenRepository.findById(loginId)
                .orElseThrow(() -> {
                    // 로그 추가: DB에 진짜 없는지 확인
                    System.out.println("디버깅 - DB에서 해당 ID를 찾을 수 없음: " + loginId);
                    return new RuntimeException("로그아웃된 사용자입니다.");
                });
//        // 3단계: DB에 저장된 토큰과 일치하는지 확인
//        RefreshToken savedToken = refreshTokenRepository.findById(loginId)
//                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));

        if (!savedToken.getToken().equals(oldRefreshToken)) {
            throw new RuntimeException("토큰 정보가 일치하지 않습니다.");
        }

        // 4단계: 새로운 토큰 세트 생성 및 DB 업데이트
        Member member = memberRepository.findByLoginId(loginId).get();
        TokenResponse newTokenSet = jwtTokenProvider.createTokenSet(member);

        savedToken.updateToken(newTokenSet.getRefreshToken());

        return newTokenSet;
    }
}