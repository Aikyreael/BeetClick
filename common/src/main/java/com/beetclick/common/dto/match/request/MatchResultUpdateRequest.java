package com.beetclick.common.dto.match.request;

import com.beetclick.common.entity.MatchResult;
import jakarta.validation.constraints.NotNull;

public record MatchResultUpdateRequest(
        @NotNull MatchResult result,
        Integer homeScore,
        Integer awayScore
) {}
