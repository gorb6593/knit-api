package com.knit.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Spring Security 기본 설정 (JWT 없이 테스트용)
     * - CSRF 해제, 모든 경로 허용(로그인/회원가입 등 오픈)
     * - 추후 JWT 인증/인가 도입 시 이 설정에서 확장
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // API 서버는 CSRF 불필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 모두 허용(로그인/회원가입)
                        //.requestMatchers("/api/posts/**").authenticated() // JWT 적용 시
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
