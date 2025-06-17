package com.knit.api.dto.user;

public record UserRegisterRequest(
        String nickname,
        String email,
        String password
) {}
