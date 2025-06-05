package com.tranner.api_gateway.filter;

import com.tranner.api_gateway.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class LoggingGlobalFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // URI + QueryParam 로그 찍기 (디버깅용)
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();

        log.info("📨 Gateway Request: [{}] {}{}", method, path, (query != null ? "?" + query : ""));

        long start = System.currentTimeMillis();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - start;
                    // 로그 남기기
                    LogUtil.logRequestDuration(log, request, duration);
                });
    }
}
