package com.example.demo.config;

import com.example.demo.security.CustomAuthenticationEntryPoint;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // REST API이므로 csrf 보안 미사용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT이므로 세션 미사용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/members/login", "/api/members/join", "/api/members/reissue", "/login", "/signup")
                        .permitAll() // 로그인/가입은 누구나
                        .requestMatchers("/css/**", "/js/**", "/images/**")
                        .permitAll() // 정적 리소스 허용
                        .anyRequest().authenticated() // 그 외는 모두 인증 필요
                )
                // 1. 에러 처리기 등록
                .exceptionHandling(handler
                        -> handler.authenticationEntryPoint(customAuthenticationEntryPoint))
                // 2. 필터 등록 (UsernamePasswordAuthenticationFilter 앞에서 동작)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // BCryptPasswordEncoder 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}