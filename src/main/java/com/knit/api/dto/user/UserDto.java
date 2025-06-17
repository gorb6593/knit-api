package com.knit.api.dto.user;

import com.knit.api.domain.user.User;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String nickname,
        String email,
        String profileImage,
        String provider,
        String providerId,
        String role,
        LocalDateTime createdAt
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage(),
                user.getProvider().name(),
                user.getProviderId(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
