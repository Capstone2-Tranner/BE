package com.tranner.external_api_proxy.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.external_api_proxy.common.exception.ErrorCode;
import com.tranner.external_api_proxy.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    public static void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.from(errorCode, path);
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}
