package com.tranner.external_api_proxy.service;

import com.tranner.external_api_proxy.type.PhotoSize;
import com.tranner.external_api_proxy.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.time.Duration;

@Service
public class PhotoService {

    private final WebClient webClient;
    private final S3Client s3Client;
    private final StringRedisTemplate redisTemplate;
    private final String GOOGLE_PLACES_KEY;
    private String REGION;
    private final String BUCKET_NAME;


    public PhotoService(WebClient webClient,
                        S3Client s3Client,
                        StringRedisTemplate redisTemplate,
                        @Value("${google.maps.api.key}") String googlePlacesKey,
                        @Value("${cloud.aws.region.static}") String region,
                        @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.webClient = webClient;
        this.s3Client = s3Client;
        this.redisTemplate = redisTemplate;
        this.GOOGLE_PLACES_KEY = googlePlacesKey;
        this.REGION = region;
        this.BUCKET_NAME = bucketName;
    }

    public Mono<String> getPhotoUrl(String photoReference, PhotoSize size) {
        String redisKey = "photo:" + size.name().toLowerCase() + ":" + photoReference;

        return Mono.fromCallable(() -> redisTemplate.opsForValue().get(redisKey))
                .flatMap(cachedUrl -> {
                    if (cachedUrl != null) return Mono.just(cachedUrl);
                    else return downloadAndCache(photoReference, redisKey, size);
                });
    }

    private Mono<String> downloadAndCache(String photoReference, String redisKey, PhotoSize size) {
        String googleUrl = buildPhotoUrl(photoReference, size.getMaxWidth());
        String s3Key = String.format("%s/photo_%s.webp", size.getS3Path(), photoReference);

        return webClient.get()
                .uri(googleUrl)
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(imageBytes -> Mono.fromCallable(() -> {
                    byte[] webpImage = ImageUtil.compressToWebP(imageBytes); // WebP 압축 로직은 따로 구현 필요

                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(s3Key)
                            .contentType("image/webp")
                            .build();

                    s3Client.putObject(request, RequestBody.fromBytes(webpImage));

                    String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                            BUCKET_NAME, REGION, s3Key);

                    redisTemplate.opsForValue()
                            .set(redisKey, s3Url, Duration.ofSeconds(size.getTtlSeconds()));

                    return s3Url;
                }));
    }

    private String buildPhotoUrl(String photoReference, int maxWidth) {
        return String.format(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photo_reference=%s&key=%s",
                maxWidth,
                photoReference,
                GOOGLE_PLACES_KEY
        );
    }
}
