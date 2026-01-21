package com.beetclick.betservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetCreatedResponse {
    private UUID id;
    private UUID userId;
    private UUID matchId;
    private BigDecimal amount;
    private BigDecimal odds;
    private BigDecimal gain;
    private String option;
}
