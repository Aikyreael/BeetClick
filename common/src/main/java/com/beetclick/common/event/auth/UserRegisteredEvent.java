package com.beetclick.common.event.auth;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        Instant createdAt
) {}
