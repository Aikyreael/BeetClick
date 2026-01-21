package com.beetclick.common.event.payment;

import com.beetclick.common.entity.PaymentCategory;

import java.time.Instant;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID paymentId,
        UUID userId,
        UUID walletId,
        double amount,
        PaymentCategory category,
        Instant processedAt
) {}