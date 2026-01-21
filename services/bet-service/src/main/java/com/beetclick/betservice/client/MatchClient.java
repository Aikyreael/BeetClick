package com.beetclick.betservice.client;

import java.util.UUID;

import com.beetclick.common.dto.match.response.MatchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MatchClient {
    private final RestTemplate restTemplate = new RestTemplate();

    private final String gatewayUrl = "http://localhost:8083";

    public MatchResponse getMatch(UUID matchId) {
        return restTemplate.getForObject(
                gatewayUrl + "/matches/{matchId}",
                MatchResponse.class,
                matchId
        );
    }


}
