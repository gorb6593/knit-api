package com.knit.api.controller.test;

import com.knit.api.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/test")
@RestController
public class TestController2 {

    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> testLogin(@RequestParam(defaultValue = "1") Long userId) {
        String token = jwtProvider.createToken(userId);
        log.info("Generated test token for user {}: {}", userId, token);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "userId", String.valueOf(userId),
            "message", "Test token generated successfully"
        ));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtProvider.getUserId(token);
            
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "valid", "true",
                "message", "Token is valid"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", "false",
                "message", "Invalid token: " + e.getMessage()
            ));
        }
    }
}