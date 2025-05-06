package com.tranner.api_gateway.exception;

public interface ErrorCode {

    int getStatus();
    String getCode();
    String getMessage();
    String getService();
}
