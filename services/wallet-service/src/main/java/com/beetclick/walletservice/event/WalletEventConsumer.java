package com.beetclick.walletservice.event;

import com.beetclick.common.event.auth.UserRegisteredEvent;
import com.beetclick.walletservice.dto.events.BetPlacedEvent;
import com.beetclick.walletservice.dto.events.PaymentSuccessEvent;
import com.beetclick.walletservice.dto.events.WithdrawSuccessEvent;
import com.beetclick.walletservice.entity.Wallet;
import com.beetclick.walletservice.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.beetclick.walletservice.service.WalletService;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.apache.kafka.common.config.ConfigResource.Type.TOPIC;

@Slf4j
@Service
public class WalletEventConsumer {
    private final WalletService walletService;

    private final WalletRepository walletRepository;

    private final ObjectMapper  objectMapper;

    public WalletEventConsumer(WalletService walletService, WalletRepository walletRepository, ObjectMapper objectMapper) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.objectMapper = objectMapper;
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

    @KafkaListener(
            id = "wallet-user-registered",
            topics = "auth.user-registered",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        String payload = record.value();

        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);

            if (walletRepository.existsByUserId(event.userId())) {
                log.info("Wallet already exists, ignoring event userId={}", event.userId());
                return;
            }

            Wallet w = new Wallet();
            w.setId(UUID.randomUUID());
            w.setUserId(event.userId());
            w.setBalance(BigDecimal.ZERO);
            w.setCoin(0);

            walletRepository.save(w);

            log.info("Wallet created from kafka event topic={} userId={}",
                    record.topic(), event.userId());

        } catch (Exception ex) {
            log.warn("Failed to process auth.user-registered payload={}", payload, ex);
        }
    }
}
