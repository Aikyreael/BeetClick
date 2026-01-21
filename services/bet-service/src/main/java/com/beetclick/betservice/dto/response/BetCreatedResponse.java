package com.beetclick.betservice.dto.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetCreatedResponse {
    private UUID id;
    private UUID userId;
    private UUID matchId;
    private double amount;
    private double odds;
    private double gain;
    private String option;
}
