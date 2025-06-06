//package com.tranner.api_gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
//import reactor.core.publisher.Mono;
//
//@Configuration
//public class JwtDecoderConfig {
//
//    @Bean
//    public ReactiveJwtDecoder reactiveJwtDecoder() {
//        return token -> Mono.error(new UnsupportedOperationException("Not using ReactiveJwtDecoder — handled by JwtUtil directly"));
//    }
//}
