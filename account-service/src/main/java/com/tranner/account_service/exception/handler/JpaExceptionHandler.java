package com.tranner.account_service.exception.handler;

import com.tranner.account_service.exception.AccountErrorCode;
import com.tranner.account_service.exception.custom.BusinessLogicException;
import com.tranner.account_service.util.LogUtil;
import com.tranner.account_service.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class JpaExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(JpaExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleBusinessLogicException(HttpServletRequest request,
                                             HttpServletResponse response,
                                             BusinessLogicException ex) throws IOException {
        // 로그 남기기
        LogUtil.logError(log, request, AccountErrorCode.DB_ERROR, ex);
        ResponseUtil.writeErrorResponse(response, ex.getCode(), request.getRequestURI());
    }

}
