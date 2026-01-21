package com.beetclick.matchservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class KafkaPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPublisher(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, UUID key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(topic, key.toString(), payload)
                    .whenComplete((res, ex) -> {
                        if (ex != null) log.warn("Kafka publish failed topic={} key={}", topic, key, ex);
                        else log.info("Kafka event sent topic={} key={}", topic, key);
                    });

        } catch (Exception ex) {
            log.warn("Kafka publish error ignored topic={} key={}", topic, key, ex);
        }
    }
}
