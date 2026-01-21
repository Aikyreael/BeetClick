package com.beetclick.betservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.beetclick.betservice.event.WalletResponse;

@Component
public class WalletClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public double getBalance(UUID userId) {
        WalletResponse response = restTemplate.getForObject(
                "http://localhost:8085/wallets/{userId}",
                WalletResponse.class,
                userId
        );
        return response.balance();
    }
}
