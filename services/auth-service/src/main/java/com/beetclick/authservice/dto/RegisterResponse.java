package com.beetclick.authservice.dto;

import com.beetclick.authservice.entity.Role;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String email,
        Role role
) {}
