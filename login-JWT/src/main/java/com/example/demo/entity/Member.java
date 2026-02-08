package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

// JPA - 이 객체가 entity임을 명시
@Entity
// Lombok
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

	// DB에서 pk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. 아이디 (로그인 식별자)
    @Column(unique = true, nullable = false)
    private String loginId;

    // 2. 비밀번호
    @Column(nullable = false)
    private String password;

    // 3. 이름
    @Column(nullable = false)
    private String name;

    // 4. 권한 (JWT 인가 및 스프링 시큐리티 연동에 필수)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 소셜 로그인 연동 시 모듈 확장을 위해 남겨두는 필드 
    // 나중에 활용
    private String provider;

     //비밀번호 암호화 후 업데이트
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    //권한 설정 업데이트
    public void updateRole(Role role) {
        this.role = role;
    }
}