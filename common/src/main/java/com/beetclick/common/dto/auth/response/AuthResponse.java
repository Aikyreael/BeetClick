package com.beetclick.common.dto.auth.response;

import com.beetclick.common.entity.Role;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        Role role,
        String accessToken,
        String refreshToken
) {}
