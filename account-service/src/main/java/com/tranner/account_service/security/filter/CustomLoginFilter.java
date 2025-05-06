package com.tranner.account_service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.account_service.dto.request.LoginRequestDTO;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.security.user.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public CustomLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

        // 로그인 요청 URL 지정 (기본은 /login)
        setFilterProcessesUrl("/account/login");
    }

    // 로그인 시도: JSON 요청에서 ID/PW 추출 → 인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            LoginRequestDTO loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.memberId(), loginRequest.password());

            return authenticationManager.authenticate(authToken); // 여기서 인증 프로세스 시작
        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }

    // 로그인 성공 → JWT 생성 & 응답에 담기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        // memberId 가져오기
        String memberId = userDetails.getUsername();

        // role 가져오기
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        /**
         * jwt 발급 엔드포인트로 redirect?
         */
        // access token 발급
        String accessToken = jwtUtil.createAccessToken(memberId, role);
        // refresh token 발급
        String refreshToken = jwtUtil.createRefreshToken(memberId);

//        // 예: accessToken은 응답 바디에, refreshToken은 HttpOnly 쿠키로
//        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of(
//                "accessToken", accessToken
//        )));
//        response.addHeader("Set-Cookie", "refreshToken=" + refreshToken + "; HttpOnly; Path=/; Max-Age=604800");
    }

    // 로그인 실패 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        // 로그인 실패 error response
    }
}
