package com.knit.api.config.security;

import com.knit.api.util.JwtProvider;
import com.knit.api.domain.user.User;
import com.knit.api.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNullApi;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {

            String token = jwtProvider.resolveToken(request);

            if (token != null && jwtProvider.validateToken(token)) {

                Claims claims = jwtProvider.parseClaims(token);
                Long userId = Long.valueOf(claims.getSubject());
                User user = userRepository.findById(userId).orElse(null);

                if (user != null) {

                    String username = user.getId().toString();

                    String role = (user.getRole() != null && !user.getRole().name().trim().isEmpty())
                            ? user.getRole().name()
                            : "USER";
                    var principal = org.springframework.security.core.userdetails.User
                            .withUsername(username)
                            .password("NO_PASSWORD") //필수로 넣어야함
                            .roles(user.getRole() != null ? user.getRole().name() : "USER")
                            .roles(role)
                            .build();

                    log.info("principal: {}", principal);

                    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("JWT 인증 성공: userId={}, email={}", userId, user.getEmail());
                }
            }
        } catch (Exception e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            response.setStatus(SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }
}
