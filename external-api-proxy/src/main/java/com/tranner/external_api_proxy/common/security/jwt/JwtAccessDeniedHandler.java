package com.tranner.external_api_proxy.common.security.jwt;

import com.tranner.external_api_proxy.common.exception.SecurityErrorCode;
import com.tranner.external_api_proxy.common.util.LogUtil;
import com.tranner.external_api_proxy.common.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, IOException {
        // 권한 거부 → 명확하게 FORBIDDEN 사용
        SecurityErrorCode errorCode = SecurityErrorCode.FORBIDDEN;

        // 로그 남기기
        LogUtil.logError(log, request, errorCode, accessDeniedException);
        // 에러 응답 보내기
        ResponseUtil.writeErrorResponse(response, errorCode, request.getRequestURI());
    }

}
