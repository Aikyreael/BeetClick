package com.beetclick.betservice.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.beetclick.betservice.event.WalletResponse;

@Component
public class WalletClient {
    private final RestTemplate restTemplate = new RestTemplate();

    private final String gatewayUrl = "http://localhost:8085";

    public BigDecimal getBalance(UUID userId) {
        WalletResponse response = restTemplate.getForObject(
                gatewayUrl + "/wallets/{userId}",
                WalletResponse.class,
                userId
        );
        return response.balance();
    }
}
