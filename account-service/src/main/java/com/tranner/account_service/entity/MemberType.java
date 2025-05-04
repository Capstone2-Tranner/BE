package com.tranner.account_service.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    OWN("OWN", "자체회원"),
    KAKAO("KAKAO", "카카오 로그인"),
    GOOGLE("GOOGLE", "구글 로그인");

    private final String code;
    private final String title;
}
