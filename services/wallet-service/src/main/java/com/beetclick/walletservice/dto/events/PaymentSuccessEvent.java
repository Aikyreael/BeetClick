package com.beetclick.walletservice.dto.events;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentSuccessEvent {
    private UUID walletId;
    private int amount;

    public PaymentSuccessEvent() {}

    public PaymentSuccessEvent(UUID walletId, int amount) {
        this.walletId = walletId;
        this.amount = amount;
    }
}
