package com.tranner.external_api_proxy.common.exception;

public enum SecurityErrorCode implements ErrorCode {

    FORBIDDEN(403, "SEC001", "접근 권한이 없습니다.", "ACCOUNT"),
    INVALID_TOKEN(401, "SEC002", "유효하지 않은 토큰입니다.", "ACCOUNT"),
    EXPIRED_TOKEN(401, "SEC003", "토큰이 만료되었습니다.", "ACCOUNT"),
    UNSUPPORTED_TOKEN(400, "SEC004", "지원하지 않는 토큰 형식입니다.", "ACCOUNT"),
    BAD_REQUEST(400, "NET001", "잘못된 요청입니다.", "ACCOUNT"),
    ROUTING_FAILED(404, "NET002", "요청 경로를 찾을 수 없습니다.", "ACCOUNT"),
    SERVICE_UNAVAILABLE(503, "SYS001", "내부 서비스 연결에 실패했습니다.", "ACCOUNT"),
    INTERNAL_SERVER_ERROR(500, "SYS999", "예상치 못한 에러가 발생했습니다.", "ACCOUNT");


    private final int status;
    private final String code;
    private final String message;
    private final String service;

    SecurityErrorCode(int status, String code, String message, String service) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.service = service;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getService() {
        return service;
    }

}
