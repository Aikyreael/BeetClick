package com.beetclick.common.dto.auth.response;


import com.beetclick.common.entity.Role;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String email,
        Role role
) {}
