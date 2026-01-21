package com.beetclick.common.event.match;

import com.beetclick.common.entity.MatchResult;

import java.util.UUID;

public record MatchFinishedEvent(
        UUID matchId,
        MatchResult result
) {}
