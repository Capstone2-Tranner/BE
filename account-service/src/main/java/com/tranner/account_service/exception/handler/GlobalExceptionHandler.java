package com.tranner.account_service.exception.handler;

import com.tranner.account_service.exception.ErrorResponse;
import com.tranner.account_service.exception.custom.BusinessLogicException;
import com.tranner.account_service.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public void handleBusinessLogicException(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      BusinessLogicException ex) throws IOException {
        ResponseUtil.writeErrorResponse(response, ex.getCode(), request.getRequestURI());
    }

}
