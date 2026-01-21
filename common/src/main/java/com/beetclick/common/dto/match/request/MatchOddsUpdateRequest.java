package com.beetclick.common.dto.match.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MatchOddsUpdateRequest(
        @NotNull BigDecimal oddsHomeWin,
        @NotNull BigDecimal oddsDraw,
        @NotNull BigDecimal oddsAwayWin
) {}
