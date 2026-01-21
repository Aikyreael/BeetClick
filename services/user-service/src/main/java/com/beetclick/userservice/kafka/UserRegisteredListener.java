package com.beetclick.userservice.kafka;

import com.beetclick.common.event.auth.UserRegisteredEvent;
import com.beetclick.userservice.entity.User;
import com.beetclick.userservice.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class UserRegisteredListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredListener.class);

    private final UserRepository repo;
    private final ObjectMapper objectMapper;

    public UserRegisteredListener(UserRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            id = "user-registered",
            topics = "auth.user-registered",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        String key = record.key();
        String payload = record.value();

        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);

            if (repo.existsById(event.userId()) || repo.existsByEmail(event.email())) {
                log.info("User already exists, ignoring kafka event userId={} email={}", event.userId(), event.email());
                return;
            }

            User u = new User();
            u.setId(event.userId());
            u.setEmail(event.email());

            repo.save(u);
            log.info("User created from kafka event topic={} key={} userId={} email={}",
                    record.topic(), key, event.userId(), event.email());

        } catch (Exception ex) {
            // Pour MVP log, DLQ ensuite plus tard chacal
            log.warn("Failed to process user-registered event topic={} key={} payload={}",
                    record.topic(), key, payload, ex);
        }
    }
}
