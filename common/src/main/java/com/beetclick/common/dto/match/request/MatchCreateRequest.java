package com.beetclick.common.dto.match.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record MatchCreateRequest(
        @NotBlank String homeTeam,
        @NotBlank String awayTeam,
        @NotNull Instant kickoffAt,
        BigDecimal oddsHomeWin,
        BigDecimal oddsDraw,
        BigDecimal oddsAwayWin
) {}
