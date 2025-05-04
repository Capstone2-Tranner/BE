package com.tranner.account_service.exception;

public enum AccountErrorCode implements ErrorCode {
    USER_NOT_FOUND(404, "A001", "존재하지 않는 사용자입니다.", "ACCOUNT"),
    DUPLICATE_EMAIL(409, "A002", "이미 존재하는 이메일입니다.", "ACCOUNT"),
    PASSWORD_MISMATCH(401, "A003", "비밀번호가 일치하지 않습니다.", "ACCOUNT");

    private final int status;
    private final String code;
    private final String message;
    private final String service;

    AccountErrorCode(int status, String code, String message, String service) {
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

