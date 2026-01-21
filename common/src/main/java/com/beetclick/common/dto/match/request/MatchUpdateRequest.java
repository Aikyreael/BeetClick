package com.beetclick.common.dto.match.request;

import java.math.BigDecimal;
import java.time.Instant;

public record MatchUpdateRequest(
        String homeTeam,
        String awayTeam,
        Instant kickoffAt,
        BigDecimal oddsHomeWin,
        BigDecimal oddsDraw,
        BigDecimal oddsAwayWin
) {}
