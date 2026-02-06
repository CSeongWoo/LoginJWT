package com.example.demo.dto;
import com.example.demo.entity.Member;
import com.example.demo.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinRequest {
    private String loginId;
    private String password;
    private String name;

    // DTO -> Entity
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .name(this.name)
                .role(Role.USER) // 기본 권한
                .build();
    }
}