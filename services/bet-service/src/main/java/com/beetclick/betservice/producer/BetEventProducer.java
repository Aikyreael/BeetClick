package com.beetclick.betservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.beetclick.betservice.event.BetPlaced;
import com.beetclick.betservice.event.BetRequested;

@Component
public class BetEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BetEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBetRequested(BetRequested event) {
        kafkaTemplate.send("bet-requests", event.betId().toString(), event);
    }

    public void publishBetPlaced(BetPlaced event) {
        kafkaTemplate.send("bet-placed", event.betId().toString(), event);
    }

    public void publishBetFailed(BetPlaced event) {
        kafkaTemplate.send("bet-failed", event.betId().toString(), event);
    }
}
