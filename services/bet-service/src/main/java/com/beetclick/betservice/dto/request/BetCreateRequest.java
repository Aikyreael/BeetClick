package com.beetclick.betservice.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetCreateRequest {
    private UUID matchId;
    private String option;
    private BigDecimal amount;
}
