package com.tranner.account_service.security.config;

import com.tranner.account_service.security.filter.CustomLoginFilter;
import com.tranner.account_service.security.jwt.JwtAccessDeniedHandler;
import com.tranner.account_service.security.jwt.JwtAuthenticationEntryPoint;
import com.tranner.account_service.security.oauth.OAuth2FailureHandler;
import com.tranner.account_service.security.oauth.OAuth2SuccessHandler;
import com.tranner.account_service.security.jwt.JwtUtil;
import com.tranner.account_service.security.oauth.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login/**", "/oauth2/**", "/login/success",
                                "/api/account/login", "/api/account/signup",
                                "/api/account/idDuplicatedCheck", "/api/account/email/verification",
                                "/api/account/email/verification/check"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(new CustomLoginFilter(authenticationConfiguration.getAuthenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(new OAuth2FailureHandler())
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver())
                        .jwt(Customizer.withDefaults())
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return new BearerTokenResolver() {
            private final DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();

            @Override
            public String resolve(HttpServletRequest request) {
                String uri = request.getRequestURI();
                System.out.println("🧪 BearerTokenResolver 호출됨 - URI: " + uri);

                if (uri.startsWith("/oauth2") || uri.startsWith("/login")) {
                    System.out.println("❎ JWT 무시 경로 → 토큰 검사 안 함");
                    return null; // JWT 인증 안 함
                }

                String token = delegate.resolve(request);
                System.out.println("✅ JWT 적용 경로 → 토큰: " + token);
                return delegate.resolve(request); // 일반 경로는 정상 인증
            }
        };
    }

}
