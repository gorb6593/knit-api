package com.knit.api.dto.common;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, LocalDateTime.now());
    }
    
    public static ErrorResponse unauthorized(String message, String path) {
        return new ErrorResponse(401, "Unauthorized", message, path, LocalDateTime.now());
    }
    
    public static ErrorResponse forbidden(String message, String path) {
        return new ErrorResponse(403, "Forbidden", message, path, LocalDateTime.now());
    }
}