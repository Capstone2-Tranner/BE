package com.tranner.api_gateway.exception.handler;

import com.tranner.api_gateway.exception.GatewayErrorCode;
import com.tranner.api_gateway.util.LogUtil;
import com.tranner.api_gateway.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        GatewayErrorCode errorCode = GatewayErrorCode.FORBIDDEN;

        LogUtil.logError(log, exchange.getRequest(), errorCode, denied);
        return ResponseUtil.writeErrorResponse(exchange.getResponse(), errorCode, exchange.getRequest().getURI().getPath());
    }
}
