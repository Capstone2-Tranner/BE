package com.tranner.api_gateway.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.api_gateway.exception.ErrorResponse;
import com.tranner.api_gateway.exception.GatewayErrorCode;
import com.tranner.api_gateway.util.LogUtil;
import com.tranner.api_gateway.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Throwable cause = authException.getCause();
        GatewayErrorCode errorCode;
        if(cause instanceof ExpiredJwtException){
            errorCode = GatewayErrorCode.EXPIRED_TOKEN;
        }else if(cause instanceof UnsupportedJwtException) {
            errorCode = GatewayErrorCode.UNSUPPORTED_TOKEN;
        }else {
            errorCode = GatewayErrorCode.INVALID_TOKEN;
        }

        // 로그 남기기
        LogUtil.logError(log, request, errorCode, cause != null ? cause : authException);
        // 에러 응답 보내기
        ResponseUtil.writeErrorResponse(response, errorCode, request.getRequestURI());

    }

}
