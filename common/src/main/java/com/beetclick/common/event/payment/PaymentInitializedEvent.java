package com.beetclick.common.event.payment;

import com.beetclick.common.entity.PaymentCategory;

import java.time.Instant;
import java.util.UUID;

public record PaymentInitializedEvent(
        UUID userId,
        UUID walletId,
        double amount,
        PaymentCategory category,
        Instant timestamp
) {}