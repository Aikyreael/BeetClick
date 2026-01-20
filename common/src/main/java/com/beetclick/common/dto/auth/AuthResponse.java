package com.beetclick.common.dto.auth;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        AuthRole role,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken
) {}
