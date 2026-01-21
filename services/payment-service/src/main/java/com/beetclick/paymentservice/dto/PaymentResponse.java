package com.beetclick.paymentservice.dto;

import java.util.UUID;


public record PaymentResponse(
        UUID userId,
        double amount,
        PaymentStatus paymentStatus,
        PaymentCategory paymentCategory
)
{
    @Override
    public UUID userId() {
        return userId;
    }

    @Override
    public double amount() {
        return amount;
    }

    @Override
    public PaymentStatus paymentStatus() {
        return paymentStatus;
    }

    @Override
    public PaymentCategory paymentCategory() {
        return paymentCategory;
    }
}
