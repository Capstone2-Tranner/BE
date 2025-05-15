package com.tranner.api_gateway.util;

import com.tranner.api_gateway.exception.ErrorCode;
import org.slf4j.Logger;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class LogUtil {

    public static void logError(Logger logger, ServerHttpRequest request, ErrorCode errorCode, Throwable ex) {
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path = request.getURI().getPath();

        if (ex != null) {
            logger.error("[ERROR {}] {} {} | code={}, message={}, exception={}",
                    errorCode.getService(),
                    method,
                    path,
                    errorCode.getCode(),
                    errorCode.getMessage(),
                    ex.getMessage()
            );
        } else {
            logger.error("[ERROR {}] {} {} | code={}, message={}",
                    errorCode.getService(),
                    method,
                    path,
                    errorCode.getCode(),
                    errorCode.getMessage()
            );
        }
    }

    public static void logWarn(Logger logger, ServerHttpRequest request, ErrorCode errorCode, Throwable ex) {
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path = request.getURI().getPath();

        if (ex != null) {
            logger.warn("[WARN {}] {} {} | code={}, message={}, exception={}",
                    errorCode.getService(),
                    method,
                    path,
                    errorCode.getCode(),
                    errorCode.getMessage(),
                    ex.getMessage()
            );
        } else {
            logger.warn("[WARN {}] {} {} | code={}, message={}",
                    errorCode.getService(),
                    method,
                    path,
                    errorCode.getCode(),
                    errorCode.getMessage()
            );
        }
    }

    public static void logRequestDuration(Logger logger, ServerHttpRequest request, long duration) {
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String uri = request.getURI().getPath();

        if (duration > 1000) {
            logger.warn("[SLOW API] {} {} took {}ms", method, uri, duration);
        } else {
            logger.info("[GATEWAY] {} {} took {}ms", method, uri, duration);
        }
    }
}
