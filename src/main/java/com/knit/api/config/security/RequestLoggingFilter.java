package com.knit.api.config.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        long startTime = System.currentTimeMillis();
        
        // 요청 정보 로깅
        log.info("🌐 [REQUEST] {} {} from {}", 
                httpRequest.getMethod(), 
                httpRequest.getRequestURI(), 
                getClientIpAddress(httpRequest));
        
        // 요청 헤더 로깅
        log.info("📋 [HEADERS] User-Agent: {}, Origin: {}, Referer: {}", 
                httpRequest.getHeader("User-Agent"),
                httpRequest.getHeader("Origin"),
                httpRequest.getHeader("Referer"));
        
        // 쿼리 파라미터 로깅
        if (httpRequest.getQueryString() != null) {
            log.info("🔍 [QUERY] {}", httpRequest.getQueryString());
        }
        
        try {
            // 다음 필터 실행
            chain.doFilter(request, response);
            
            long duration = System.currentTimeMillis() - startTime;
            
            // 응답 정보 로깅
            log.info("✅ [RESPONSE] {} {} -> {} ({}ms)", 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(), 
                    httpResponse.getStatus(), 
                    duration);
                    
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ [ERROR] {} {} -> ERROR ({}ms): {}", 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(), 
                    duration, 
                    e.getMessage());
            throw e;
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        return clientIp;
    }
}