package com.knit.api.controller.user;

import com.knit.api.domain.user.User;
import com.knit.api.dto.user.UserDto;
import com.knit.api.dto.user.UserRegisterRequest;
import com.knit.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
