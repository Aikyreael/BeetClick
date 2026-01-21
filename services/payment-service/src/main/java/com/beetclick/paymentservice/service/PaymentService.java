package com.beetclick.paymentservice.service;

import com.beetclick.common.entity.PaymentCategory;
import com.beetclick.common.entity.PaymentStatus;
import com.beetclick.common.event.payment.PaymentFailedEvent;
import com.beetclick.common.event.payment.PaymentInitializedEvent;
import com.beetclick.common.event.payment.PaymentSuccessEvent;
import com.beetclick.paymentservice.client.WalletClient;
import com.beetclick.paymentservice.dto.PaymentCreditRequest;
import com.beetclick.paymentservice.dto.PaymentResponse;
import com.beetclick.paymentservice.entity.Payment;
import com.beetclick.paymentservice.exception.PaymentNotFoundException;
import com.beetclick.paymentservice.persistence.PaymentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WalletClient walletClient;

    private static final double MIN_DEPOSIT = 10.0;
    private static final double DAILY_LIMIT = 1000.0;

    private static final String TOPIC_PAYMENT_INITIALIZED = "payment.initialized";
    private static final String TOPIC_PAYMENT_SUCCESS = "payment.success";
    private static final String TOPIC_PAYMENT_FAILED = "payment.failed";

    public PaymentService(
            PaymentRepository paymentRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            WalletClient walletClient
    ) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.walletClient = walletClient;
    }

    public List<PaymentResponse> getAllPayments() {
        List<Payment> paymentList = paymentRepository.findAll();
        return paymentList.stream()
                .map(payment -> new PaymentResponse(
                        payment.getUserId(),
                        payment.getAmount(),
                        payment.getStatus(),
                        payment.getCategory()
                ))
                .toList();
    }

    public Payment getPaymentById(UUID userId) {
        Payment payment = paymentRepository.findById(userId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for userId: " + userId));

        return new Payment(
                payment.getAmount(),
                payment.getCategory(),
                payment.getStatus()
        );
    }

    @Transactional
    public Payment creditWallet(PaymentCreditRequest request, UUID userId) {
        publishInitializedEvent(userId, request.walletId(), request.amount(), PaymentCategory.CREDIT);

        if (request.amount() < MIN_DEPOSIT) {
            publishFailedEvent(userId, request.walletId(), request.amount(), PaymentCategory.CREDIT, "Minimum deposit is 10€");
            throw new IllegalArgumentException("Minimum deposit is 10€");
        }

        double todayTotal = paymentRepository.sumTodayCredits(
                request.walletId(),
                LocalDate.now().atStartOfDay()
        );

        if (todayTotal + request.amount() > DAILY_LIMIT) {
            publishFailedEvent(userId, request.walletId(), request.amount(), PaymentCategory.CREDIT, "Daily limit exceeded");
            throw new IllegalStateException("Daily limit exceeded (1000€)");
        }

        Payment payment = new Payment();
        payment.setWalletId(request.walletId());
        payment.setUserId(userId);
        payment.setAmount(request.amount());
        payment.setCategory(PaymentCategory.CREDIT);
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment saved = paymentRepository.save(payment);

        publishSuccessEvent(saved);

        return saved;
    }

    @Transactional
    public Payment withdrawWallet(PaymentCreditRequest request, UUID userId) {
        publishInitializedEvent(userId, request.walletId(), request.amount(), PaymentCategory.WITHDRAWAL);

        double balance = walletClient.getBalance(request.walletId());

        if (balance < request.amount()) {
            publishFailedEvent(userId, request.walletId(), request.amount(), PaymentCategory.WITHDRAWAL, "Insufficient balance");
            throw new IllegalStateException("Insufficient balance");
        }

        Payment payment = new Payment();
        payment.setWalletId(request.walletId());
        payment.setUserId(userId);
        payment.setAmount(-(request.amount()));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCategory(PaymentCategory.WITHDRAWAL);

        Payment saved = paymentRepository.save(payment);

        publishSuccessEvent(saved);

        return saved;
    }

    private void publishInitializedEvent(UUID userId, UUID walletId, double amount, PaymentCategory category) {
        PaymentInitializedEvent event = new PaymentInitializedEvent(
                userId,
                walletId,
                amount,
                category,
                Instant.now()
        );
        kafkaTemplate.send(TOPIC_PAYMENT_INITIALIZED, event);
    }

    private void publishSuccessEvent(Payment payment) {
        PaymentSuccessEvent event = new PaymentSuccessEvent(
                payment.getId(),
                payment.getUserId(),
                payment.getWalletId(),
                payment.getAmount(),
                payment.getCategory(),
                Instant.now()
        );
        kafkaTemplate.send(TOPIC_PAYMENT_SUCCESS, event);
    }

    private void publishFailedEvent(UUID userId, UUID walletId, double amount, PaymentCategory category, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent(
                userId,
                walletId,
                amount,
                category,
                reason,
                Instant.now()
        );
        kafkaTemplate.send(TOPIC_PAYMENT_FAILED, event);
    }
}