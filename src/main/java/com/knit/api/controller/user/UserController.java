package com.knit.api.controller.user;

import com.knit.api.domain.user.User;
import com.knit.api.dto.user.UserDto;
import com.knit.api.dto.user.UserProfileUpdateRequest;
import com.knit.api.dto.user.UserRegisterRequest;
import com.knit.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입(임시, 실제는 소셜/비밀번호 등 분리 필요)
    @PostMapping
    public UserDto create(@RequestBody UserRegisterRequest req) {
        User user = User.local(req.nickname(), req.email(), req.password(), User.Role.USER);
        return UserDto.from(userService.save(user));
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = Long.valueOf(authentication.getName());
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return ResponseEntity.ok(UserDto.from(user));
    }

    // 프로필 수정 (닉네임 + 프로필 이미지)
    @PostMapping("/profile/with-image")
    public ResponseEntity<UserDto> updateProfileWithImage(
            Authentication authentication,
            @RequestPart(value = "data", required = false) String userData,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }
            
            // JSON 문자열을 파싱해서 닉네임 추출
            String nickname = null;
            if (userData != null && !userData.trim().isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(userData);
                    if (jsonNode.has("nickname")) {
                        nickname = jsonNode.get("nickname").asText();
                    }
                } catch (Exception e) {
                    // JSON 파싱 실패 시 무시
                }
            }
            
            Long userId = Long.valueOf(authentication.getName());
            UserProfileUpdateRequest request = new UserProfileUpdateRequest(nickname);
            User updatedUser = userService.updateProfile(userId, request, profileImage);
            return ResponseEntity.ok(UserDto.from(updatedUser));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
