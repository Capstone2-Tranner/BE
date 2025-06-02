package com.tranner.external_api_proxy.common.exception;

public interface ErrorCode {

    int getStatus();
    String getCode();
    String getMessage();
    String getService();

}
