package com.beetclick.common.event.match;

import java.time.Instant;
import java.util.UUID;

public record MatchEvent(
        String type,
        UUID matchId,
        Instant at
) {}
