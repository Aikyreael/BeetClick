package com.beetclick.betservice.event;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse (UUID betId, UUID userId, BigDecimal balance) {}
