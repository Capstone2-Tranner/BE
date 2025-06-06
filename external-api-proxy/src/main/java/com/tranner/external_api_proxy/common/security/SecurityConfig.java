package com.tranner.external_api_proxy.common.security;

//import com.tranner.external_api_proxy.common.security.jwt.CustomJwtDecoder;
import com.tranner.external_api_proxy.common.security.jwt.JwtAccessDeniedHandler;
import com.tranner.external_api_proxy.common.security.jwt.JwtAuthenticationEntryPoint;
import com.tranner.external_api_proxy.common.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    //private final CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/discovery/**").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver()) // 너가 정의한 커스텀 BearerTokenResolver
//                        .jwt(jwt -> jwt
//                                .jwtDecoder(customJwtDecoder) // 위에서 만든 CustomJwtDecoder
//                        )
                        .jwt(Customizer.withDefaults())
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
                    return null;
                }

                String token = delegate.resolve(request);
                System.out.println("✅ JWT 적용 경로 → 토큰: " + token);
                return token;
            }
        };
    }
}
