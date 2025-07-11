package com.knit.api.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knit.api.dto.common.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        log.warn("Access denied: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.forbidden(
                "접근 권한이 없습니다. 관리자에게 문의하세요.",
                request.getRequestURI()
        );
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}