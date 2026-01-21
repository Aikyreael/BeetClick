package com.beetclick.paymentservice.dto;

import com.beetclick.common.entity.PaymentCategory;
import com.beetclick.common.entity.PaymentStatus;

import java.util.UUID;

public record PaymentResponse(
        UUID userId,
        double amount,
        PaymentStatus paymentStatus,
        PaymentCategory paymentCategory
) {}