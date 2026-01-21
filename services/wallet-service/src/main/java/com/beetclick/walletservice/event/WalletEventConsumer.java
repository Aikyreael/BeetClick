package com.beetclick.walletservice.event;

import com.beetclick.walletservice.dto.events.PaymentSuccessEvent;
import com.beetclick.walletservice.dto.events.WithdrawSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.beetclick.walletservice.service.WalletService;

@Service
public class WalletEventConsumer {
    private final WalletService walletService;

    public WalletEventConsumer(WalletService walletService) {
        this.walletService = walletService;
    }

    @KafkaListener(topics = "payment.success", groupId = "wallet-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        walletService.creditWallet(event.getWalletId(), event.getAmount());
    }

    @KafkaListener(topics = "withdraw.success", groupId = "wallet-group")
    public void handleWithdrawSuccess(WithdrawSuccessEvent event) {
        walletService.debitWallet(event.getWalletId(), event.getAmount());
    }
}
