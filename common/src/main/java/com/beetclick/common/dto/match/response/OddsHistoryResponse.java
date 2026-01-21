package com.beetclick.common.dto.match.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OddsHistoryResponse(
        UUID id,
        UUID matchId,
        BigDecimal oddsHomeWin,
        BigDecimal oddsDraw,
        BigDecimal oddsAwayWin,
        Instant createdAt
) {}
