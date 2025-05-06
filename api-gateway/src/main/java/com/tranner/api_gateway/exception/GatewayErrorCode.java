package com.tranner.api_gateway.exception;

public enum GatewayErrorCode implements  ErrorCode{
    FORBIDDEN(403, "G001", "접근 권한이 없습니다.", "GATEWAY"),
    INVALID_TOKEN(401, "G002", "유효하지 않은 토큰입니다.", "GATEWAY"),
    EXPIRED_TOKEN(401, "G003", "토큰이 만료되었습니다.", "GATEWAY"),
    UNSUPPORTED_TOKEN(400, "G004", "지원하지 않는 토큰 형식입니다.", "GATEWAY"),
    BAD_REQUEST(400, "G005", "잘못된 요청입니다.", "GATEWAY"),
    ROUTING_FAILED(404, "G006", "요청 경로를 찾을 수 없습니다.", "GATEWAY"),
    SERVICE_UNAVAILABLE(503, "G007", "내부 서비스 연결에 실패했습니다.", "GATEWAY"),
    INTERNAL_SERVER_ERROR(500, "G999", "예상치 못한 에러가 발생했습니다.", "GATEWAY");


    private final int status;
    private final String code;
    private final String message;
    private final String service;

    GatewayErrorCode(int status, String code, String message, String service) {
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
