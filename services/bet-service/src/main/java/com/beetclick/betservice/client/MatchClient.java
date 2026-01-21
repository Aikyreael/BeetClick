package com.beetclick.betservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MatchClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public MatchResponse getMatch(UUID matchId) {
        MatchResponse response = restTemplate.getForObject(
                "http://localhost:8083/matches/{matchId}",
                MatchResponse.class,
                matchId
        );
        return response;
    }

    public record MatchResponse(UUID id, String status, double oddsTeam1, double oddsDraw, double oddsTeam2) {}

}
