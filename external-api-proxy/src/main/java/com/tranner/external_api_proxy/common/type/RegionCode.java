package com.tranner.external_api_proxy.common.type;

import java.util.Arrays;

public enum RegionCode {
    // 일본
    오사카(1, "Osaka"),
    도쿄(2, "Tokyo"),
    후쿠오카(3, "Fukuoka"),
    삿포로(4, "Sapporo"),
    // 중국
    베이징(1, "Beijing"),
    상하이(2, "Shanghai"),
    // 태국
    방콕(1, "Bangkok"),
    치앙마이(2, "Chiang Mai"),
    코사무이(3, "Ko Samui"),
    // 미국
    뉴욕(1, "New York"),
    하와이(2, "Hawaii"),
    괌(3, "Guam"),
    LA(4, "Los Angeles"),
    샌프란시스코(5, "San Francisco"),
    사이판(6, "Saipan"),
    // 이탈리아
    로마(1, "Rome"),
    베니스(2, "Venice"),
    밀라노(3, "Milan"),
    나폴리(4, "Naples"),
    // 스페인
    바르셀로나(1, "Barcelona"),
    마드리드(2, "Madrid"),
    // 대한민국
    제주도(1, "Jeju"),
    서울(2, "Seoul"),
    부산(3, "Busan"),
    경주(4, "Gyeongju"),
    강릉(5, "Gangneung");

    private final int code;
    private final String englishName;

    RegionCode(int code, String englishName) {
        this.code = code;
        this.englishName = englishName;
    }

    public int getCode() {
        return code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public static RegionCode fromCode(int code) {
        return Arrays.stream(values())
                .filter(r -> r.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid region code: " + code));
    }

    public static RegionCode fromName(String name) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역명입니다: " + name));
    }

    public static String getEnglishNameFromKorean(String koreanName) {
        return fromName(koreanName).getEnglishName();
    }
}
