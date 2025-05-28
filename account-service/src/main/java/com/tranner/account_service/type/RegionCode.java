package com.tranner.account_service.type;

import java.util.Arrays;

public enum RegionCode {
    //일본
    오사카(1),
    도쿄(2),
    후쿠오카(3),
    삿포로(4),
    //중국
    베이징(1),
    상하이(2),
    //태국
    방콕(1),
    치앙마이(2),
    코사무이(3),
    //미국
    뉴욕(1),
    하와이(2),
    괌(3),
    LA(4),
    샌프란시스코(5),
    사이판(6),
    //이탈리아
    로마(1),
    베니스(2),
    밀라노(3),
    나폴리(4),
    //스페인
    바르셀로나(1),
    마드리드(2),
    //대한민국
    제주도(1),
    서울(2),
    부산(3),
    경주(4),
    강릉(5);

    private final int code;

    RegionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RegionCode fromCode(int code) {
        for (RegionCode r : values()) {
            if (r.code == code) return r;
        }
        throw new IllegalArgumentException("Invalid region code: " + code);
    }

    public static RegionCode fromName(String name) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역명입니다: " + name));
    }
}
