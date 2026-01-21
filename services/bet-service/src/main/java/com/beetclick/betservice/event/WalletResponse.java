package com.beetclick.betservice.event;

import java.util.UUID;

public record WalletResponse (UUID betId, UUID userId, double balance) {}
