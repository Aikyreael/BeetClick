package com.beetclick.paymentservice.event;

import com.beetclick.common.event.payment.PaymentFailedEvent;
import com.beetclick.common.event.payment.PaymentInitializedEvent;
import com.beetclick.common.event.payment.PaymentSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    @KafkaListener(topics = "payment.success", groupId = "payment-group")
    public void onSuccess(PaymentSuccessEvent event) {
        log.info("Payment success: paymentId={}, userId={}, amount={}",
                event.paymentId(), event.userId(), event.amount());
    }

    @KafkaListener(topics = "payment.failed", groupId = "payment-group")
    public void onFailed(PaymentFailedEvent event) {
        log.warn("Payment failed: userId={}, reason={}", event.userId(), event.reason());
    }

    @KafkaListener(topics = "payment.initialized", groupId = "payment-group")
    public void onInitialized(PaymentInitializedEvent event) {
        log.info("Payment initialized: userId={}, walletId={}, amount={}",
                event.userId(), event.walletId(), event.amount());
    }
}
