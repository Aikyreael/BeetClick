package com.beetclick.betservice.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.beetclick.betservice.event.BetFailed;
import com.beetclick.betservice.event.BetPlaced;
import com.beetclick.betservice.event.BetRequested;

@Component
public class BetEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public BetEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Not yet used : Can be used to avoid HTTP requests between services by asking wallet & match from kafka events
    public void publishBetRequested(BetRequested event) {
        kafkaTemplate.send("bet.requests", event.betId().toString(), event);
    }

    public void publishBetPlaced(BetPlaced event) {
        kafkaTemplate.send("bet.placed", event);
    }

    // Not yet used
    public void publishBetFailed(BetFailed event) {
        kafkaTemplate.send("bet.failed", event.betId().toString(), event);
    }
}
