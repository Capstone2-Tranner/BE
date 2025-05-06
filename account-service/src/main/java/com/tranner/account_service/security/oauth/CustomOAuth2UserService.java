package com.tranner.account_service.security.oauth;

import com.tranner.account_service.domain.Member;
import com.tranner.account_service.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 1. registrationId: 소셜 로그인 제공자 구분 (google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2. userNameAttributeName: OAuth2 제공자의 사용자 식별 키 (예: google - "sub", kakao - "id")
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 3. OAuth2 사용자 정보 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        // 4. 사용자 저장 또는 기존 사용자 조회
        Member member = saveOrUpdate(oAuthAttributes);

        // 사용자 인증 수단으로 session을 사용할 경우에 사용
        //httpSession.setAttribute("client", new SessionUser(client));

        // 5. OAuth2User 반환 (SecurityContext에 저장됨)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                oAuthAttributes.getAttributes(),
                oAuthAttributes.getNameAttributeKey()
        );
    }

    /**
     * 가입된 회원이면 그대로 반환하고,
     * 처음 로그인하는 사용자면 새로 등록 후 반환
     */
    private Member saveOrUpdate(OAuthAttributes oAuthAttributes) {
        return memberRepository.findByEmail(oAuthAttributes.getEmail())
                .orElseGet(() -> memberRepository.save(oAuthAttributes.toEntity()));
    }
}
