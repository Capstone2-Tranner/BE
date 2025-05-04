package com.tranner.account_service.exception;

public enum AccountErrorCode implements ErrorCode {
    // 이메일 관련
    DUPLICATE_EMAIL(409, "CLT001", "이미 존재하는 이메일입니다.", "ACCOUNT"),
    INVALID_EMAIL_FORMAT(400, "CLT002", "유효하지 않은 이메일 형식입니다.", "ACCOUNT"),
    EMAIL_NOT_REGISTERED(404, "CLT003", "등록되지 않은 이메일입니다.", "ACCOUNT"),
    UNABLE_TO_SEND_EMAIL(500, "SYS001", "이메일 전송에 실패했습니다.", "ACCOUNT"),

    // 아이디 관련
    USER_NOT_FOUND(404, "CLT004", "사용자를 찾을 수 없습니다.", "ACCOUNT"),
    USERID_EXISTS(409, "CLT005", "이미 존재하는 ID입니다.", "ACCOUNT"),

    // 인증 코드 관련
    NO_SUCH_ALGORITHM(500, "SYS002", "안전한 랜덤한 값을 생성할 수 없습니다.", "ACCOUNT"),
    INVALID_VERIFICATION_CODE(400, "CLT006", "유효하지 않은 인증 코드입니다.", "ACCOUNT"),
    VERIFICATION_CODE_EXPIRED(400, "CLT007", "인증 코드가 만료되었습니다.", "ACCOUNT"),

    // 로그인 관련
    FAILED_LOGIN(401, "CLT008", "회원정보가 일치하지 않습니다.", "ACCOUNT"),
    PASSWORD_MISMATCH(401, "A003", "비밀번호가 일치하지 않습니다.", "ACCOUNT"),

    // JPA 관련
    DB_ERROR(500, "SYS003", "데이터 저장 중 오류가 발생했습니다.", "ACCOUNT")
    ;

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

