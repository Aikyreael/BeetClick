package com.beetclick.walletservice.event;

import com.beetclick.walletservice.dto.events.BetPlacedEvent;
import com.beetclick.walletservice.dto.events.PaymentSuccessEvent;
import com.beetclick.walletservice.dto.events.WithdrawSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.beetclick.walletservice.service.WalletService;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class WalletEventConsumer {
    private final WalletService walletService;

    public WalletEventConsumer(WalletService walletService) {
        this.walletService = walletService;
    }

    @KafkaListener(topics = "payment.success", groupId = "wallet-group")
    public void handlePaymentSuccess(String message) {
        ObjectMapper mapper = new ObjectMapper();
        PaymentSuccessEvent event = mapper.readValue(message, PaymentSuccessEvent.class);

        log.info("PaymentSuccessEvent reçu: walletId={}, amount={}", event.getWalletId(), event.getAmount());
        walletService.creditWallet(event.getWalletId(), event.getAmount());
    }

    @KafkaListener(topics = "withdraw.success", groupId = "wallet-group")
    public void handleWithdrawSuccess(String message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        WithdrawSuccessEvent event = mapper.readValue(message, WithdrawSuccessEvent.class);

        log.info(
                "WithdrawSuccessEvent reçu: walletId={}, amount={}",
                event.getWalletId(),
                event.getAmount()
        );

        walletService.debitWalletByWalletId(event.getWalletId(), event.getAmount());
    }

    @KafkaListener(topics = "bet.placed", groupId = "wallet-group")
    public void handleBetPlaced(String message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BetPlacedEvent event = mapper.readValue(message, BetPlacedEvent.class);

        log.info(
                "BetPlacedEvent reçu: userID={}, amount={}",
                event.getUserId(),
                event.getAmount()
        );
        walletService.debitWalletByUserId(event.getUserId(), event.getAmount());
    }
}
