package com.tranner.external_api_proxy.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;
    private final String path;

    public ErrorResponse(int status, String code, String message, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public static ErrorResponse from(ErrorCode errorCode, String path) {
        return new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path
        );
    }
}
