package com.tranner.external_api_proxy.api.exception;

import com.tranner.external_api_proxy.common.exception.ErrorCode;

public enum ApiErrorCode implements ErrorCode {

    GOOGLE_HTTP_ERROR(500, "SYS001", "Google API 요청 과정에서 HTTP 오류가 발생했습니다.", "API-PROXY"),
    GOOGLE_API_REQUEST_ERROR(500, "SYS002", "Google API 요청 과정에 기타 오류가 발생했습니다.", "API-PROXY"),
    INTERNAL_SERVER_ERROR(500, "SYS999", "예상치 못한 에러가 발생했습니다.", "API-PROXY");


    private final int status;
    private final String code;
    private final String message;
    private final String service;

    ApiErrorCode(int status, String code, String message, String service) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.service = service;
    }

    @Override public int getStatus() { return status; }
    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public String getService() { return service; }
}
