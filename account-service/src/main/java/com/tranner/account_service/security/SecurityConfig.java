package com.tranner.account_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf disable
                .csrf(csrf -> csrf.disable())
                // cors 설정
                // form 로그인 비활성화
                .formLogin((auth) -> auth.disable())
                // http basic 인증 비활성화
                .httpBasic((auth) -> auth.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/account/login", "/account/signup").permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 인증 방식 적용 (Spring Security가 토큰을 자동 검증)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                // 인증/인가 실패 시 처리할 핸들러 지정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        return http.build();
    }


}
