package com.beetclick.common.dto.match.request;

import com.beetclick.common.entity.MatchStatus;
import jakarta.validation.constraints.NotNull;

public record MatchStatusUpdateRequest(@NotNull MatchStatus status) {}
