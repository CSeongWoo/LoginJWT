package com.example.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	// 로그인 시 ID로 회원을 찾기 위한 메서드
    Optional<Member> findByLoginId(String loginId);
}
