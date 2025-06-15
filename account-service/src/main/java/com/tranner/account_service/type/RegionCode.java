package com.tranner.account_service.type;

import com.tranner.account_service.type.CountryCode;

import java.util.Arrays;

public enum RegionCode {
    // 일본
    오사카(CountryCode.일본,1, "Osaka"),
    도쿄(CountryCode.일본,2, "Tokyo"),
    후쿠오카(CountryCode.일본,3, "Fukuoka"),
    삿포로(CountryCode.일본,4, "Sapporo"),
    // 중국
    베이징(CountryCode.중국, 1, "Beijing"),
    상하이(CountryCode.중국, 2, "Shanghai"),
    // 태국
    방콕(CountryCode.태국, 1, "Bangkok"),
    치앙마이(CountryCode.태국, 2, "Chiang Mai"),
    코사무이(CountryCode.태국, 3, "Ko Samui"),
    // 미국
    뉴욕(CountryCode.미국, 1, "New York"),
    하와이(CountryCode.미국, 2, "Hawaii"),
    괌(CountryCode.미국, 3, "Guam"),
    LA(CountryCode.미국, 4, "Los Angeles"),
    샌프란시스코(CountryCode.미국, 5, "San Francisco"),
    사이판(CountryCode.미국, 6, "Saipan"),
    // 이탈리아
    로마(CountryCode.이탈리아, 1, "Rome"),
    피렌체(CountryCode.이탈리아, 2, "Florence"),
    베니스(CountryCode.이탈리아, 3, "Venice"),
    밀라노(CountryCode.이탈리아, 4, "Milan"),
    나폴리(CountryCode.이탈리아, 5, "Naples"),
    // 스페인
    바르셀로나(CountryCode.스페인, 1, "Barcelona"),
    마드리드(CountryCode.스페인, 2, "Madrid"),
    // 대한민국
    서울(CountryCode.대한민국,1, "Seoul"),
    부산(CountryCode.대한민국,2, "Busan"),
    제주(CountryCode.대한민국,3, "Jeju"),
    경주(CountryCode.대한민국,4, "Gyeongju"),
    강릉(CountryCode.대한민국,5, "Gangneung"),
    전주(CountryCode.대한민국,6, "Jeonju"),
    수원(CountryCode.대한민국,7, "Suwon");

    private final CountryCode countryCode;
    private final int code;
    private final String englishName;

    RegionCode(CountryCode countryCode, int code, String englishName) {
        this.countryCode = countryCode;
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

    public static RegionCode fromCountryAndCode(CountryCode country, int code) {
        return Arrays.stream(values())
                .filter(r -> r.countryCode == country && r.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("매핑 안됨 " + code));
    }


    public static String getEnglishNameFromKorean(String koreanName) {
        return fromName(koreanName).getEnglishName();
    }
}
