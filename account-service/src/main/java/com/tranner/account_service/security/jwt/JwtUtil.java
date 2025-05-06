package com.tranner.account_service.security.jwt;

import com.tranner.account_service.service.RedisService;
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
    private String secret;

    @Getter
    @Value("${jwt.access-token-validity}")
    private long accessTokenExpirationMs;
    @Getter
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenExpirationMs;

    private final RedisService redisService;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    /**
     * Access Token 생성 (JWT)
     */
    public String createAccessToken(String memberId, String role) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성 (UUID → Redis 저장)
     */
    public String createRefreshToken(String memberId) {
        String refreshToken = UUID.randomUUID().toString();
        redisService.saveRefreshToken(memberId, refreshToken, refreshTokenExpirationMs);
        return refreshToken;
    }

    /**
     * 토큰에서 memberId 추출
     */
    public String getMemberId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", String.class);
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
