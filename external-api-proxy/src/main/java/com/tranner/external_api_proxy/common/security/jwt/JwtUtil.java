package com.tranner.external_api_proxy.common.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private SecretKey secretKey;

    @Value("${jwt.secret}")
    @Getter
    private String secret;

    @Getter
    @Value("${jwt.access-token-validity}")
    private long accessTokenExpirationMs;
    @Getter
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenExpirationMs;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    /**
     * 토큰에서 memberId 추출
     */
    public String getMemberId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("memberId", String.class);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이라도 claims는 꺼낼 수 있음
            return e.getClaims().get("memberId", String.class);
        }
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    /**
     * 만료 여부 체크
     */
    public boolean isExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());

    }
}
