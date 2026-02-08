package com.example.demo.dto;

import com.example.demo.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class JoinRequest {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private Role role; // 선택 사항 (없으면 서비스에서 USER로 할당)
}