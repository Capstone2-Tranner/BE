package com.tranner.account_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String EMAIL_VERIFICATION_PREFIX = "verify:";

    /** ===========================
     *  Refresh Token 관련 메서드
     *  =========================== */

    public void saveRefreshToken(String memberId, String token, long expirationMinutes) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + memberId,
                token,
                Duration.ofMinutes(expirationMinutes)
        );
    }

    public String getRefreshToken(String memberId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + memberId);
    }

    public void deleteRefreshToken(String memberId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + memberId);
    }

    public boolean isValidRefreshToken(String memberId, String token) {
        String saved = getRefreshToken(memberId);
        return saved != null && saved.equals(token);
    }

    /** ================================
     *  이메일 인증 코드 관련 메서드
     *  ================================ */

    public void saveEmailVerificationCode(String email, String code, long expirationMinutes) {
        redisTemplate.opsForValue().set(
                EMAIL_VERIFICATION_PREFIX + email,
                code,
                Duration.ofMinutes(expirationMinutes)
        );
    }

    public String getEmailVerificationCode(String email) {
        return redisTemplate.opsForValue().get(EMAIL_VERIFICATION_PREFIX + email);
    }

    public void deleteEmailVerificationCode(String email) {
        redisTemplate.delete(EMAIL_VERIFICATION_PREFIX + email);
    }
}

