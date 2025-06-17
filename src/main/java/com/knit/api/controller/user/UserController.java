package com.knit.api.controller.user;

import com.knit.api.domain.user.User;
import com.knit.api.dto.user.UserDto;
import com.knit.api.dto.user.UserRegisterRequest;
import com.knit.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 이메일로 회원 조회 (임시)
    @GetMapping("/email")
    public UserDto findByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(UserDto::from)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // 회원가입(임시, 실제는 소셜/비밀번호 등 분리 필요)
    @PostMapping
    public UserDto create(@RequestBody UserRegisterRequest req) {
        User user = User.local(req.nickname(), req.email(), req.password(), User.Role.USER);
        return UserDto.from(userService.save(user));
    }

    @GetMapping("/test")
    public String test() {
        return "test11";
    }
}
