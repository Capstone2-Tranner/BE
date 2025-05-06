package com.tranner.account_service.exception.handler;

import com.tranner.account_service.exception.custom.BusinessLogicException;
import com.tranner.account_service.exception.custom.InterenalServerException;
import com.tranner.account_service.util.LogUtil;
import com.tranner.account_service.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
@ControllerAdvice
public class ServerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ServerExceptionHandler.class);

    @ExceptionHandler(InterenalServerException.class)
    public void handleInterenalServerException(HttpServletRequest request,
                                             HttpServletResponse response,
                                             InterenalServerException ex) throws IOException {
        // 로그 남기기
        LogUtil.logError(log, request, ex.getCode(), ex);
        // 에러 응답 보내기
        ResponseUtil.writeErrorResponse(response, ex.getCode(), request.getRequestURI());
    }
    
}
