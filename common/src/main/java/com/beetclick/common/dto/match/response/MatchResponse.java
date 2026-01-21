package com.beetclick.common.dto.match.response;


import com.beetclick.common.entity.MatchResult;
import com.beetclick.common.entity.MatchStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MatchResponse(
        UUID id,
        String homeTeam,
        String awayTeam,
        Instant kickoffAt,
        MatchStatus status,
        MatchResult result,
        Integer homeScore,
        Integer awayScore,
        BigDecimal oddsHomeWin,
        BigDecimal oddsDraw,
        BigDecimal oddsAwayWin,
        Instant createdAt,
        Instant updatedAt,
        int version
) {}
