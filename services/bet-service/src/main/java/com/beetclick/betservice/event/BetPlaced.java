package com.beetclick.betservice.event;

import java.util.UUID;

public record BetPlaced(UUID userId, UUID matchId, double amount) {}