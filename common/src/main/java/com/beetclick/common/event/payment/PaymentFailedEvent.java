package com.beetclick.common.event.payment;

import com.beetclick.common.entity.PaymentCategory;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID userId,
        UUID walletId,
        double amount,
        PaymentCategory category,
        String reason,
        Instant timestamp
) {}