package com.knit.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/1")
    public String test() {
        return "test!!";
    }

    @GetMapping("/2")
    public String test2() {
        return "한글!!";
    }

    @GetMapping("/3")
    public String test3() {
        return "테스트3";
    }

    @GetMapping("/auth")
    public String testAuth(@AuthenticationPrincipal UserDetails userDetails) {
        return "인증된 사용자: " + userDetails.getUsername() + ", 권한: " + userDetails.getAuthorities();
    }

    @GetMapping("/user")
    public String testUser(Authentication authentication) {
        return "USER 권한 테스트 - 사용자: " + authentication.getName();
    }
}
