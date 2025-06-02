package com.tranner.external_api_proxy.api.service;

import com.tranner.external_api_proxy.common.type.PhotoSize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class PhotoService {

    private final WebClient webClient;
    private final StringRedisTemplate redisTemplate;
    private final String GOOGLE_PLACES_KEY;
    private final String BUCKET_NAME; // 현재는 사용되지 않지만, 추후 확장을 고려해 유지
    private final String REGION;

    public PhotoService(WebClient webClient,
                        StringRedisTemplate redisTemplate,
                        @Value("${google.maps.api.key}") String googlePlacesKey,
                        @Value("${cloud.aws.region.static}") String region,
                        @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
        this.GOOGLE_PLACES_KEY = googlePlacesKey;
        this.REGION = region;
        this.BUCKET_NAME = bucketName;
    }

    /**
     * 캐시된 URL이 있으면 반환, 없으면 Google 이미지 URL 생성 후 캐시
     */
    public String getPhotoUrl(String photoReference, PhotoSize size) {
        String googleUrl = buildPhotoUrl(photoReference, size.getMaxWidth());
        return googleUrl;

//        String redisKey = "photo:" + size.name().toLowerCase() + ":" + photoReference;
//
//        return Mono.fromCallable(() -> {
//                    String cached = redisTemplate.opsForValue().get(redisKey);
//                    System.out.println("[PhotoService] Redis 조회 key: " + redisKey + ", 결과: " + cached);
//                    return cached;
//                })
//                .flatMap(cachedUrl -> {
//                    if (cachedUrl != null) {
//                        System.out.println("[PhotoService] 캐시 HIT - " + redisKey);
//                        return Mono.just(cachedUrl);
//                    } else {
//                        System.out.println("[PhotoService] 캐시 MISS - " + redisKey);
//                        return getAndCache(photoReference, redisKey, size);
//                    }
//                })
//                .onErrorResume(e -> {
//                    System.out.println("[PhotoService] Redis 접근 중 에러 발생: " + e.getMessage());
//                    e.printStackTrace();
//                    return Mono.error(new RuntimeException("Redis 접근 중 오류 발생", e));
//                });
    }

//
//    /**
//     * Redis에 캐시하고 Google 이미지 URL 반환
//     */
//    private Mono<String> getAndCache(String photoReference, String redisKey, PhotoSize size) {
//        String googleUrl = buildPhotoUrl(photoReference, size.getMaxWidth());
//
//        return Mono.fromRunnable(() -> {
//                    redisTemplate.opsForValue()
//                            .set(redisKey, googleUrl, Duration.ofSeconds(size.getTtlSeconds()));
//                    log.info("[PhotoService] Redis 캐시 저장 - key: {}, ttl: {}초", redisKey, size.getTtlSeconds());
//                })
//                .thenReturn(googleUrl)
//                .onErrorResume(e -> {
//                    log.error("[PhotoService] Redis 캐시 저장 중 에러 발생: {}", e.getMessage(), e);
//                    return Mono.just(googleUrl); // fallback: 그래도 URL은 리턴
//                });
//    }

    /**
     * Google Places Photo API용 URL 생성
     */
    private String buildPhotoUrl(String photoReference, int maxWidth) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photo_reference=%s&key=%s",
                maxWidth,
                photoReference,
                GOOGLE_PLACES_KEY
        );
        log.debug("[PhotoService] Google Photo URL 생성: {}", url);
        return url;
    }
}
