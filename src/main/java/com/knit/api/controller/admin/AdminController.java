package com.knit.api.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public String testAdmin(Authentication authentication) {
        return "ADMIN 권한 테스트 - 관리자: " + authentication.getName();
    }

    @GetMapping("/users")
    public String getAllUsers(Authentication authentication) {
        return "모든 사용자 조회 - 관리자: " + authentication.getName();
    }
}