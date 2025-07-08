package com.knit.api.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knit.api.domain.user.UserAccessLog;
import com.knit.api.repository.user.UserAccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiAccessLogger {

    private final UserAccessLogRepository accessLogRepository;
    private final ObjectMapper objectMapper;

    @AfterReturning("within(@org.springframework.web.bind.annotation.RestController *)")
    public void logApiAccess(JoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();

        // 로그인 정보
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        // 파라미터 수집
        Map<String, String[]> params = request.getParameterMap();
        String queryParams = "";
        try { queryParams = objectMapper.writeValueAsString(params); } catch (Exception ignored) {}

        // IP, User-Agent, Referer 등
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        // DB 저장
        UserAccessLog logEntity = new UserAccessLog();
        logEntity.setUserId(userId);
        logEntity.setMethod(request.getMethod());
        logEntity.setEndpoint(request.getRequestURI());
        logEntity.setQueryParams(queryParams);
        logEntity.setIp(ip);
        logEntity.setUserAgent(userAgent);
        logEntity.setReferer(referer);
        accessLogRepository.save(logEntity);

    }
}
