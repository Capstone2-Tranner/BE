package com.tranner.account_service.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ADMIN", "관리자"),
    USER("USER", "사용자");

    private final String key;
    private final String title;
}
