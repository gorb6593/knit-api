package com.knit.api.dto.auth;

public record KakaoUserInfo(
        String nickname,
        String email,
        String providerId,
        String profileImage
) {}
