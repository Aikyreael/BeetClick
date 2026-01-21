package com.beetclick.paymentservice.dto;

import java.util.UUID;

public record PaymentCreditRequest(
        UUID walletId,
        double amount
) {}