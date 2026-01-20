package com.beetclick.betservice.event;

import java.util.UUID;

public record BetFailed(UUID betId, String reason) {}
