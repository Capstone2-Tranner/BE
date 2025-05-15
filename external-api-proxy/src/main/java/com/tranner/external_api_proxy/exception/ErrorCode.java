package com.tranner.external_api_proxy.exception;

public interface ErrorCode {

    int getStatus();
    String getCode();
    String getMessage();
    String getService();

}
