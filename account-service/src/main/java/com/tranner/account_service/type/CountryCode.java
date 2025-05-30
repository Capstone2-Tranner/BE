package com.tranner.account_service.type;

public enum CountryCode {
    대한민국(82),
    일본(81),
    중국(86),
    태국(66),
    미국(1),
    이탈리아(39),
    스페인(34);

    private final int code;

    CountryCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CountryCode fromCode(int code) {
        for (CountryCode c : values()) {
            if (c.code == code) return c;
        }
        throw new IllegalArgumentException("Invalid country code: " + code);
    }

    public static CountryCode fromName(String name) {
        for (CountryCode c : values()) {
            if (c.name().equals(name)) return c;
        }
        throw new IllegalArgumentException("존재하지 않는 국가명입니다: " + name);
    }
}

