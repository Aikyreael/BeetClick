package com.beetclick.authservice.dto;

import com.beetclick.authservice.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        Role role,
        String accessToken,
        String refreshToken
) {}
