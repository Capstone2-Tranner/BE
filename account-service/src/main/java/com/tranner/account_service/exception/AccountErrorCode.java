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
    INVALID_MEMBER_ID(400, "CLT006", "아이디는 영문자 또는 숫자 5~20자리여야 합니다.", "ACCOUNT"),

    // 인증 코드 관련
    NO_SUCH_ALGORITHM(500, "SYS002", "안전한 랜덤한 값을 생성할 수 없습니다.", "ACCOUNT"),
    INVALID_VERIFICATION_CODE(400, "CLT007", "인증 코드는 6자리입니다.", "ACCOUNT"),
    WRONG_VERIFICATION_CODE(400, "CLT008", "인증 코드가 잘못되었습니다.", "ACCOUNT"),
    VERIFICATION_CODE_EXPIRED(400, "CLT009", "인증 코드가 만료되었습니다.", "ACCOUNT"),

    // 로그인 관련
    FAILED_LOGIN(401, "CLT010", "회원정보가 일치하지 않습니다.", "ACCOUNT"),
    OAUTH2_LOGIN_FAILED(401, "SEC005", "소셜 로그인에 실패했습니다.", "ACCOUNT"),

    // 비밀번호 관련
    PASSWORD_MISMATCH(401, "CLT011", "비밀번호가 일치하지 않습니다.", "ACCOUNT"),
    INVALID_PASSWORD(400, "CLT013", "비밀번호는 영문자, 숫자, 특수문자를 포함한 8~20자리여야 합니다.", "ACCOUNT"),

    // 엑세스 토큰 재발급 관련
    MISSING_REFRESH_TOKEN(401, "SEC001", "Refresh Token이 존재하지 않습니다.", "ACCOUNT"),
    MISSING_ACCESS_TOKEN(401, "SEC002", "Access Token이 필요합니다.", "ACCOUNT"),
    INVALID_ACCESS_TOKEN(401, "SEC003", "Access Token이 유효하지 않습니다.", "ACCOUNT"),
    INVALID_REFRESH_TOKEN(401, "SEC004", "유효하지 않은 Refresh Token입니다.", "ACCOUNT"),

    // JPA 관련
    DB_ERROR(500, "SYS003", "데이터 저장 중 오류가 발생했습니다.", "ACCOUNT"),

    //REDIS 관련
    REDIS_FAILED(503, "RDS001", "REDIS 연동에 문제가 발생했습니다.", "ACCOUNT"),

    // 기타
    MISSING_FIELD(400, "CLT000", "필수 값을 입력해주세요.", "COMMON")
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

