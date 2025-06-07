package com.tranner.account_service.security.oauth;

import com.tranner.account_service.type.Role;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private static final String REDIRECT_URI = "https://api.tranner.com/login/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String email;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
        } else if ("google".equals(registrationId)) {
            email = (String) oAuth2User.getAttributes().get("email");
        } else {
            throw new RuntimeException("지원하지 않는 로그인 제공자입니다: " + registrationId);
        }

        String memberId = email.substring(0, email.indexOf("@"));

        System.out.println("OAuth Success: " + registrationId + " / " + email);

        // 1. Access Token 생성
        String accessToken = jwtUtil.createAccessToken(memberId, Role.ROLE_USER.getKey());

        // 2. Refresh Token 생성
        String refreshToken = jwtUtil.createRefreshToken(memberId);

        // 3. Redis 저장 (memberId → refreshToken)
        redisService.saveRefreshToken(memberId, refreshToken, jwtUtil.getRefreshTokenExpirationMs());

        // 4. Refresh Token을 HTTP-Only 쿠키에 저장
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true); // JS에서 접근 못하게 함
//        refreshTokenCookie.setSecure(true);   // HTTPS 환경에서만 전송되도록 설정 (로컬 테스트 시 false 가능)
//        refreshTokenCookie.setPath("/");      // 모든 경로에서 접근 가능
//        refreshTokenCookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpirationMs() / 1000)); // 만료 시간 (초 단위)
//
//        response.addCookie(refreshTokenCookie);
        String cookieString = "refreshToken=" + refreshToken +
                "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=" +
                (jwtUtil.getRefreshTokenExpirationMs() / 1000);

        response.setHeader("Set-Cookie", cookieString);


        System.out.println("MOVE TO REDIRECT");
        // 5. 토큰 포함해서 리다이렉트 (query param 또는 header)
        String redirectUri = REDIRECT_URI + "?accessToken=" + accessToken;
        redirectStrategy.sendRedirect(request, response, redirectUri);

    }
}

