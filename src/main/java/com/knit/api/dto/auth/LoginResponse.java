package com.knit.api.dto.auth;

import com.knit.api.dto.user.UserDto;

public record LoginResponse(
        String token,
        UserDto user
) { }
