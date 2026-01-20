package com.beetclick.betservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class MatchClient {
    private final WebClient webClient;

    public MatchClient(WebClient webClient) {
        this.webClient = builder.baseUrl("http://localhost/8081").build();
    }

    public MatchResponse getMatch(UUID matchId) {
        return webClient.get()
            .uri("/matches/{id}", matchId)
            .retrieve()
            .bodyToMono(MatchResponse.class)
            .block();
    }

    public record MatchResponse(UUID id, String status, double oddsTeam1, double oddsDraw, double oddsTeam2) {}

}
