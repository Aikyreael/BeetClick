package com.beetclick.betservice.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BetPlaced(UUID userId, UUID matchId, BigDecimal amount) {}