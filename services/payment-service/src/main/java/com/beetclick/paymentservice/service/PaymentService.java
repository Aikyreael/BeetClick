package com.beetclick.paymentservice.service;

import com.beetclick.paymentservice.client.WalletClient;
import com.beetclick.paymentservice.dto.*;
import com.beetclick.paymentservice.entity.Payment;
import com.beetclick.paymentservice.exception.PaymentNotFoundException;
import com.beetclick.paymentservice.persistence.PaymentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        kafkaTemplate.send("payment.initialized", request);

        if (request.amount() < MIN_DEPOSIT) {
            publishFailedEvent("Minimum deposit is 10€");
            throw new IllegalArgumentException("Minimum deposit is 10€");
        }

        double todayTotal = paymentRepository.sumTodayCredits(
                request.walletId(),
                LocalDate.now().atStartOfDay()
        );

        if (todayTotal + request.amount() > DAILY_LIMIT) {
            publishFailedEvent("Daily limit exceeded");
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
        publishInitializedEvent(request);

        double balance = walletClient.getBalance(request.walletId());

        if (balance < request.amount()) {
            publishFailedEvent("Insufficient balance");
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

    private void publishInitializedEvent(PaymentCreditRequest request) {
        kafkaTemplate.send("payment.initialized", request);
    }

    private void publishSuccessEvent(Payment payment) {
        kafkaTemplate.send("payment.success", payment);
    }

    private void publishFailedEvent(String reason) {
        kafkaTemplate.send("payment.failed", reason);
    }
}