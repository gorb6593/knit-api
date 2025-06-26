package com.knit.api.config.security;

import com.knit.api.repository.user.UserRepository;
import com.knit.api.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/login",
                                "/test/**",
                                "/api/todos/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                ).addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                );

//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/**").permitAll() // 모두 허용(로그인/회원가입)
//                        //.requestMatchers("/api/posts/**").authenticated() // JWT 적용 시
//                        .anyRequest().permitAll()
//                );
        return http.build();
    }
}
