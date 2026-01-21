package com.beetclick.paymentservice.client;

import com.beetclick.paymentservice.exception.WalletServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class WalletClient {

    private static final Logger log = LoggerFactory.getLogger(WalletClient.class);
    private final RestClient restClient;

    public WalletClient(@Value("${wallet-service.url:http://wallet-service:8080}") String walletServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(walletServiceUrl)
                .build();
    }

    public double getBalance(UUID walletId) {
        try {
            Double balance = restClient.get()
                    .uri("/wallets/{walletId}/balance", walletId)
                    .retrieve()
                    .body(Double.class);
            return balance != null ? balance : 0.0;
        } catch (Exception e) {
            log.error("Wallet service unavailable: {}", e.getMessage());
            throw new WalletServiceUnavailableException("Unable to retrieve balance from wallet service", e);
        }
    }
}
