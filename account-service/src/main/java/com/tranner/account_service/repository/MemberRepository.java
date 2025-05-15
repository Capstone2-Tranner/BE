package com.tranner.account_service.repository;

import com.tranner.account_service.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId); // 아이디로 멤버 조회

    Boolean existsByMemberId(String memberId); // 아이디 중복 확인

    Optional<Member> findByEmail(String email); // 이메일로 멤버 아이디 조회

    Boolean existsByEmail(String email); // 이미 존재하는 이메일인지 확인

}
