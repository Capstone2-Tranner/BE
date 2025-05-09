package com.tranner.account_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            throw new UnsupportedOperationException("Not using JwtDecoder — using JwtUtil directly");
        };
    }
}

