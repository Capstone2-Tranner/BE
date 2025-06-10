package com.tranner.external_api_proxy.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.external_api_proxy.api.dto.response.DetailSearchResult;
import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration TEXT_SEARCH_TTL = Duration.ofHours(24);
    private static final Duration DETAIL_SEARCH_TTL = Duration.ofHours(12);

    private static final String PREFIX_TEXT = "text:";
    private static final String PREFIX_TEXT_LOC = "text:loc:";
    private static final String PREFIX_DETAIL = "detail:";

    // ✅ Text Search 캐싱
    public void cacheTextSearch(String textQuery, String pageToken, V1KeywordSearchResponse response) {
        try {
            String encodedQuery = URLEncoder.encode(textQuery, StandardCharsets.UTF_8); // 공백 등 인코딩
            String key = PREFIX_TEXT + encodedQuery + ":" + (pageToken != null ? pageToken : "first");
            String value = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, value, TEXT_SEARCH_TTL);
        } catch (Exception e) {
            log.error("❌ Text Search Redis 캐싱 실패", e);
        }
    }

    public V1KeywordSearchResponse getCachedTextSearch(String textQuery, String pageToken) {
        try {
            String encodedQuery = URLEncoder.encode(textQuery, StandardCharsets.UTF_8);
            String key = PREFIX_TEXT + encodedQuery + ":" + (pageToken != null ? pageToken : "first");
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            return objectMapper.readValue(value, V1KeywordSearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Text Search Redis 조회 실패", e);
            return null;
        }
    }

    public Mono<V1KeywordSearchResponse> getCachedTextSearchMono(String textQuery, String pageToken) {
        return Mono.fromCallable(() -> {
            try {
                String encodedQuery = URLEncoder.encode(textQuery, StandardCharsets.UTF_8);
                String key = PREFIX_TEXT + encodedQuery + ":" + (pageToken != null ? pageToken : "first");
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) return null;

                V1KeywordSearchResponse result = objectMapper.readValue(value, V1KeywordSearchResponse.class);
                return result;
            } catch (Exception e) {
                log.error("❌ Text Search Redis 조회 실패", e);
                return null;
            }
        });
    }

    // ✅ Text Search 캐싱
    public void cacheTextSearchLoc(String textQuery, Double latitude, Double longitude, String pageToken, V1KeywordSearchResponse response) {
        try {
            String encodedQuery = URLEncoder.encode(textQuery, StandardCharsets.UTF_8); // 공백 등 인코딩
            String latStr = String.format("%.6f", latitude);
            String lngStr = String.format("%.6f", longitude);
            String key = PREFIX_TEXT_LOC + encodedQuery + ":" + latStr + ":" + lngStr + ":" + (pageToken != null ? pageToken : "first");

            String value = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, value, TEXT_SEARCH_TTL);
        } catch (Exception e) {
            log.error("❌ Text Search Redis 캐싱 실패", e);
        }
    }

    public Mono<V1KeywordSearchResponse> getCachedTextSearchLocMono(String textQuery, Double latitude, Double longitude, String pageToken) {
        return Mono.fromCallable(() -> {
            try {
                String encodedQuery = URLEncoder.encode(textQuery, StandardCharsets.UTF_8);
                String latStr = String.format("%.6f", latitude);
                String lngStr = String.format("%.6f", longitude);
                String key = PREFIX_TEXT_LOC + encodedQuery + ":" + latStr + ":" + lngStr + ":" + (pageToken != null ? pageToken : "first");
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) return null;

                V1KeywordSearchResponse result = objectMapper.readValue(value, V1KeywordSearchResponse.class);
                return result;
            } catch (Exception e) {
                log.error("❌ Text Search Redis 조회 실패", e);
                return null;
            }
        });
    }


    // ✅ Detail Search 캐싱
    public void cacheDetail(String placeId, DetailSearchResult detail) {
        try {
            String key = PREFIX_DETAIL + placeId;
            String value = objectMapper.writeValueAsString(detail);
            redisTemplate.opsForValue().set(key, value, DETAIL_SEARCH_TTL);
        } catch (Exception e) {
            log.error("❌ Detail Redis 캐싱 실패", e);
        }
    }

    public DetailSearchResult getCachedDetail(String placeId) {
        try {
            String key = PREFIX_DETAIL + placeId;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            return objectMapper.readValue(value, DetailSearchResult.class);
        } catch (Exception e) {
            log.error("❌ Detail Redis 조회 실패", e);
            return null;
        }
    }

    public Mono<DetailSearchResult> getCachedDetailMono(String placeId) {
        return Mono.fromCallable(() -> getCachedDetail(placeId));
    }

    public void cacheDetailAsync(String placeId, DetailSearchResult detail) {
        try {
            String key = PREFIX_DETAIL + placeId;
            String value = objectMapper.writeValueAsString(detail);
            redisTemplate.opsForValue().set(key, value, DETAIL_SEARCH_TTL);
        } catch (Exception e) {
            log.error("❌ Detail Redis 캐싱 실패", e);
        }
    }

}