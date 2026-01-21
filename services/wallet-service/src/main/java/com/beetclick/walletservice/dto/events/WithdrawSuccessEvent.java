package com.beetclick.walletservice.dto.events;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WithdrawSuccessEvent {

    private UUID walletId;
    private int amount;

    public WithdrawSuccessEvent() {}

    public WithdrawSuccessEvent(UUID walletId, int amount) {
        this.walletId = walletId;
        this.amount = amount;
    }

}
