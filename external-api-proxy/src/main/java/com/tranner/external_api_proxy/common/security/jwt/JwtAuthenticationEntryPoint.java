package com.tranner.external_api_proxy.common.security.jwt;

import com.tranner.external_api_proxy.common.exception.SecurityErrorCode;
import com.tranner.external_api_proxy.common.util.LogUtil;
import com.tranner.external_api_proxy.common.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, IOException {

        System.out.println("🔥 JwtAuthenticationEntryPoint 진입 - URI: " + request.getRequestURI());
        System.out.println("🔥 예외 메시지: " + authException.getMessage());
        Throwable cause = authException.getCause();
        SecurityErrorCode errorCode;
        if(cause instanceof ExpiredJwtException){
            errorCode = SecurityErrorCode.EXPIRED_TOKEN;
        }else if(cause instanceof UnsupportedJwtException) {
            errorCode = SecurityErrorCode.UNSUPPORTED_TOKEN;
        }else {
            errorCode = SecurityErrorCode.INVALID_TOKEN;
        }

        // 로그 남기기
        LogUtil.logError(log, request, errorCode, cause != null ? cause : authException);
        // 에러 응답 보내기
        ResponseUtil.writeErrorResponse(response, errorCode, request.getRequestURI());

    }

}
