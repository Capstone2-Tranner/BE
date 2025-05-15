package com.tranner.account_service.exception.handler;

import com.tranner.account_service.exception.custom.InternalServerException;
import com.tranner.account_service.util.LogUtil;
import com.tranner.account_service.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ServerExceptionHandler {


    @ExceptionHandler(InternalServerException.class)
    public void handleInterenalServerException(HttpServletRequest request,
                                             HttpServletResponse response,
                                             InternalServerException ex) throws IOException {
        // 로그 남기기
        LogUtil.logError(log, request, ex.getCode(), ex);
        // 에러 응답 보내기
        ResponseUtil.writeErrorResponse(response, ex.getCode(), request.getRequestURI());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<?> handleRedisConnectionFailure(RedisConnectionFailureException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", 503,
                        "code", "REDIS001",
                        "message", "Redis 서버에 연결할 수 없습니다. 관리자에게 문의하세요.",
                        "detail", ex.getMessage()
                ));
    }
    
}
