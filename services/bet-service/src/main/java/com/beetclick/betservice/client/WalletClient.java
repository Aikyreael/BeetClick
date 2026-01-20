package com.beetclick.betservice.client;

import org.springframework.stereotype.Component;

import com.beetclick.betservice.event.WalletResponse;

@Component
public class WalletClient {
    private final WebClient webClient;

    public WalletClient(WebClient webClient) {
        this.webClient = builder.baseUrl("http://localhost/8080").build();
    }

    public double getBalance(UUID userId) {
        return webClient.get()
            .uri("/wallets/{userId}", userId)
            .retrieve()
            .bodyToMono(WalletResponse.class)
            .block()
            .balance();
    }
}
