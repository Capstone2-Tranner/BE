package com.tranner.account_service.security.oauth;

import com.tranner.account_service.exception.AccountErrorCode;
import com.tranner.account_service.exception.SecurityErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import com.tranner.account_service.util.LogUtil;
import com.tranner.account_service.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        // 1. 에러 코드 정의 (권한 인증 실패에 해당)
        AccountErrorCode errorCode = AccountErrorCode.OAUTH2_LOGIN_FAILED;

        // 2. 로그 찍기
        LogUtil.logError(log, request, errorCode, exception);

        System.out.println("❌ OAuth2 Failure Handler invoked");
        System.out.println("❌ Request URI: " + request.getRequestURI());
        System.out.println("❌ Exception: " + exception.getClass().getName());
        System.out.println("❌ Message: " + exception.getMessage());
        exception.printStackTrace(); // ✅ stack trace도 찍어서 원인 정확히 파악

        // 3. 응답 처리
        ResponseUtil.writeErrorResponse(response, errorCode, request.getRequestURI());
    }
}

