package com.tranner.account_service.exception.handler;

import com.tranner.account_service.exception.AccountErrorCode;
import com.tranner.account_service.exception.ErrorResponse;
import com.tranner.account_service.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidation(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          MethodArgumentNotValidException ex) throws IOException {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        if (fieldError == null) {
            ResponseUtil.writeErrorResponse(response, AccountErrorCode.MISSING_FIELD, request.getRequestURI());
        }

        String field = fieldError.getField();
        String defaultMessage = fieldError.getDefaultMessage();

        AccountErrorCode errorCode = resolveErrorCode(field, defaultMessage);

        ResponseUtil.writeErrorResponse(response, errorCode, request.getRequestURI());
    }

    private AccountErrorCode resolveErrorCode(String field, String message) {
        return switch (field) {
            case "memberId" -> AccountErrorCode.INVALID_MEMBER_ID;
            case "password" -> AccountErrorCode.INVALID_PASSWORD;
            case "memberEmail" -> AccountErrorCode.INVALID_EMAIL_FORMAT;
            case "verificationCode" -> AccountErrorCode.INVALID_VERIFICATION_CODE;
            default -> AccountErrorCode.MISSING_FIELD;
        };
    }

}
