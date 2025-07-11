package com.knit.api.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knit.api.dto.common.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("Authentication failed: {} - {}", request.getRequestURI(), authException.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.unauthorized(
                "인증이 필요합니다. 로그인 후 이용해주세요.",
                request.getRequestURI()
        );
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}