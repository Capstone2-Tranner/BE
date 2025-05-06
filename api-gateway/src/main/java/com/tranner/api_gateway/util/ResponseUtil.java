package com.tranner.api_gateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.api_gateway.exception.ErrorCode;
import com.tranner.api_gateway.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {
    public static void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, String path) throws  IOException {
        ErrorResponse errorResponse = ErrorResponse.from(errorCode, path);
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
