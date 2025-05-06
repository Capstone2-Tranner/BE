package com.tranner.account_service.security.config;

import com.tranner.account_service.security.filter.CustomLoginFilter;
import com.tranner.account_service.security.jwt.JwtAccessDeniedHandler;
import com.tranner.account_service.security.jwt.JwtAuthenticationEntryPoint;
import com.tranner.account_service.security.oauth.OAuth2FailureHandler;
import com.tranner.account_service.security.oauth.OAuth2SuccessHandler;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public CustomLoginFilter customLoginFilter() throws Exception {
        return new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

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
                // 자체 로그인 (JWT 기반) - 커스텀 필터는 아래에 추가
                .addFilterBefore(customLoginFilter(), UsernamePasswordAuthenticationFilter.class)

                // 소셜 로그인
                .oauth2Login(oauth -> oauth
                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        // 로그인 성공 시 핸들러
                        .successHandler(oAuth2SuccessHandler)
                        // 로그인 실패 시 핸들러
                        .failureHandler(new OAuth2FailureHandler())
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
