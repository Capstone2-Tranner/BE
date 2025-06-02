package com.tranner.external_api_proxy.common.exception.custom;

import com.tranner.external_api_proxy.api.exception.ApiErrorCode;

public class BusinessLogicException extends RuntimeException {

    private final ApiErrorCode code; // 예외 코드
    private final String detailMessage;

    // 생성자: 예외 코드만 사용하는 경우
    public BusinessLogicException(ApiErrorCode code) {
        super(code.getMessage()); // 상위 클래스의 메시지에 예외 코드의 메시지를 전달
        this.code = code;
        this.detailMessage = null; // 세부 메시지가 없는 경우
    }

    // 생성자: 예외 코드와 추가적인 메시지를 함께 사용하는 경우
    public BusinessLogicException(ApiErrorCode code, String detailMessage) {
        super(code.getMessage() + ": " + detailMessage); // 상위 클래스의 메시지에 예외 코드의 메시지와 추가적인 메시지를 전달
        this.code = code;
        this.detailMessage = detailMessage;
    }

    // 예외 코드만 반환
    public ApiErrorCode getCode() {
        return code;
    }

    // HTTP 상태 코드 반환
    public int getStatus() {
        return code.getStatus(); // ExceptionCode에서 상태 코드를 가져옴
    }

    // 세부 메시지만 반환
    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // 성능 최적화: 스택 트레이스 생략
    }

}
