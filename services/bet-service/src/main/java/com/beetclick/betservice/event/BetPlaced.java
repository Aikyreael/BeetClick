package com.beetclick.betservice.event;

import java.util.UUID;

public record BetPlaced(UUID betId, UUID userId, UUID matchId, double amount, double odds, double gain) {}