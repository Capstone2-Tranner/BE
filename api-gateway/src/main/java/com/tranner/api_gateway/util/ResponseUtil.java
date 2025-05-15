package com.tranner.api_gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.api_gateway.exception.ErrorCode;
import com.tranner.api_gateway.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Mono<Void> writeErrorResponse(ServerHttpResponse response, ErrorCode errorCode, String path) {
        ErrorResponse errorResponse = ErrorResponse.from(errorCode, path);

        try {
            byte[] responseBody = objectMapper.writeValueAsBytes(errorResponse);

            response.setStatusCode(HttpStatus.valueOf(errorCode.getStatus()));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.getHeaders().setContentLength(responseBody.length);

            return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody)));
        } catch (Exception e) {
            // fallback: JSON 변환 실패 시 빈 응답
            return response.setComplete();
        }
    }
}
