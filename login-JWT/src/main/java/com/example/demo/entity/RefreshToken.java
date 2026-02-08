package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String loginId; // 사용자 식별값
    private String token;   // Refresh Token 문자열

    public RefreshToken(String loginId, String token) {
        this.loginId = loginId;
        this.token = token;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}