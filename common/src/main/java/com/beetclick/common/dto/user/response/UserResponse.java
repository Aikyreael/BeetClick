package com.beetclick.common.dto.user.response;

import com.beetclick.common.entity.Rank;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String countryCode,
        Rank rank
) {}
