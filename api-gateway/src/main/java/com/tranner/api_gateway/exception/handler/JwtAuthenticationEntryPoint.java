package com.tranner.api_gateway.exception.handler;

import com.tranner.api_gateway.exception.GatewayErrorCode;
import com.tranner.api_gateway.util.LogUtil;
import com.tranner.api_gateway.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint; // ✅ 진짜 이거!
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint { // ✅ 바뀐 인터페이스

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        Throwable cause = ex.getCause();
        GatewayErrorCode errorCode;

        if (cause instanceof ExpiredJwtException) {
            errorCode = GatewayErrorCode.EXPIRED_TOKEN;
        } else if (cause instanceof UnsupportedJwtException) {
            errorCode = GatewayErrorCode.UNSUPPORTED_TOKEN;
        } else {
            errorCode = GatewayErrorCode.INVALID_TOKEN;
        }

        LogUtil.logError(log, request, errorCode, cause != null ? cause : ex);
        return ResponseUtil.writeErrorResponse(response, errorCode, request.getURI().getPath());
    }
}
