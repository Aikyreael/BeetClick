package com.beetclick.paymentservice.Event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment.success", groupId = "payment-group")
    public void onSuccess(Object message) {
        System.out.println("✅ [CONSUMER] payment.success received: " + message);
    }

    @KafkaListener(topics = "payment.failed", groupId = "payment-group")
    public void onFailed(Object message) {
        System.out.println("❌ [CONSUMER] payment.failed received: " + message);
    }

    @KafkaListener(topics = "payment.initialized", groupId = "payment-group")
    public void onInitialized(Object message) {
        System.out.println("🚀 [CONSUMER] payment.initialized received: " + message);
    }
}
