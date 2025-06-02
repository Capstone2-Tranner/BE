package com.tranner.external_api_proxy.api.exception;

public interface ErrorCode {

    int getStatus();
    String getCode();
    String getMessage();
    String getService();

}
