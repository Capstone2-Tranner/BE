//package com.tranner.external_api_proxy.common.security.jwt;
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import org.springframework.security.oauth2.core.OAuth2Error;
//import org.springframework.security.oauth2.jwt.*;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.time.Instant;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class CustomJwtDecoder implements JwtDecoder {
//
//    private final SecretKeySpec secretKey;
//
//    public CustomJwtDecoder(JwtUtil jwtUtil) {
//        this.secretKey = new SecretKeySpec(jwtUtil.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//    }
//
//    @Override
//    public Jwt decode(String token) throws JwtException {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            return new Jwt(
//                    token,
//                    claims.getIssuedAt().toInstant(),
//                    claims.getExpiration().toInstant(),
//                    Map.of("alg", "HS256"), // header
//                    claims // claims 전체를 attributes로
//            );
//
//        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//            throw new JwtValidationException("JWT expired", List.of(new OAuth2Error("token_expired", e.getMessage(), null)));
//        } catch (Exception e) {
//            throw new JwtValidationException("Invalid JWT", List.of(new OAuth2Error("invalid_token", e.getMessage(), null)));
//        }
//    }
//}