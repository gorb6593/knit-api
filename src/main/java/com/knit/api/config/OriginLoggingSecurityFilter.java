package com.knit.api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class OriginLoggingSecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String origin = req.getHeader("Origin");
        String remoteAddr = req.getRemoteAddr();
        String uri = req.getRequestURI();

        log.info("[SECURITY-CORS-LOG] Origin: {}, RemoteAddr: {}, URI: {}", origin, remoteAddr, uri);

        chain.doFilter(request, response);
    }
}
