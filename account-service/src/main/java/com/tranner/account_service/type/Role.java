package com.tranner.account_service.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_ADMIN("ROLE_ADMIN", "관리자"),
    ROLE_USER("ROLE_USER", "사용자");

    private final String key;
    private final String title;

    public static Role from(String key) {
        for (Role role : Role.values()) {
            if (role.getKey().equalsIgnoreCase(key)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role key: " + key);
    }
}
