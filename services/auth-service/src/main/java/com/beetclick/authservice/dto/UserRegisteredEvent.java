package com.beetclick.authservice.dto;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        Instant createdAt
) {}
