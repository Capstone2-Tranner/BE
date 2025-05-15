package com.tranner.account_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id; // 멤버 식별자

    @Column(name = "member_id", nullable = false, unique = true, length = 20)
    private String memberId; // 멤버 아이디

    @Column(name = "member_pw", nullable = true, length = 255)
    private String password; // 멤버 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false, length = 10)
    private MemberType memberType; // 자체회원, 카카오, 구글 등

    @Column(name = "member_email", length = 100)
    private String email; // 이메일 (nullable)

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 10)
    private Role role; // 관리자, 사용자

    @Column(name = "register_date")
    private LocalDate registerDate; // 가입일자
}
