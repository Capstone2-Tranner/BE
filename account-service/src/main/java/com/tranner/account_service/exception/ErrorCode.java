package com.tranner.account_service.exception;

public interface ErrorCode {

    int getStatus();
    String getCode();
    String getMessage();
    String getService();

}
