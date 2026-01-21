package com.beetclick.common.dto.user.response;

import com.beetclick.common.entity.Rank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(
        @Pattern(regexp = "^[A-Z]{2}$", message = "countryCode must be ISO2 (ex: FR)")
        String countryCode,
        Rank rank,
        String role
) {}
