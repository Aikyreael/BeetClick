package com.beetclick.betservice.event;

import java.util.UUID;

public record BetRequested(UUID betId, UUID userId, UUID matchId, double amount, String option) {}