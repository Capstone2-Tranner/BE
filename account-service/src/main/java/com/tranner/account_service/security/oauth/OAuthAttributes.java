package com.tranner.account_service.security.oauth;

import com.tranner.account_service.domain.Member;
import com.tranner.account_service.type.MemberType;
import com.tranner.account_service.type.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
public class OAuthAttributes {

    private final String registrationId;
    private final String nameAttributeKey;
    private final Map<String, Object> attributes;
    private final String email;

    @Builder
    public OAuthAttributes(String registrationId, String nameAttributeKey, Map<String, Object> attributes, String email) {
        this.registrationId = registrationId;
        this.nameAttributeKey = nameAttributeKey;
        this.attributes = attributes;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        /*
        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        }
        */
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuthAttributes.builder()
                .email((String) kakaoAccount.get("email"))
                .registrationId("kakao")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .email((String) attributes.get("email"))
                .registrationId("google")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
/*
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .registrationId("naver")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
*/
    // 가입되지 않은 사용자인 경우, 회원 가입 위해 사용
    public Member toEntity() {
        return Member.builder()
                .memberId(email.substring(0, email.indexOf("@")))
                .email(email)
                .role(Role.USER)  // enum Role.USER 사용 예시
                .memberType(convertToMemberType(registrationId))
                .registerDate(LocalDate.now())
                .build();
    }

    private MemberType convertToMemberType(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "kakao" -> MemberType.KAKAO;
            case "google" -> MemberType.GOOGLE;
            //case "naver" -> MemberType.NAVER;
            default -> MemberType.OWN; // 자체 회원가입
        };
    }

}
