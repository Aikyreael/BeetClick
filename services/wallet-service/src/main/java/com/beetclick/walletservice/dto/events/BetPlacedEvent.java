package com.beetclick.walletservice.dto.events;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BetPlacedEvent {

    private UUID userId;
    private UUID matchId;
    private int amount;

    public BetPlacedEvent(UUID userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
