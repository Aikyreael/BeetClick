package com.beetclick.userservice.debug;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class KafkaListenersDebug implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(KafkaListenersDebug.class);
    private final KafkaListenerEndpointRegistry registry;

    public KafkaListenersDebug(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run(ApplicationArguments args) {
        var containers = registry.getListenerContainers();
        log.info("Kafka listeners containers count={}", containers.size());

        containers.forEach(c -> log.info(
                "Kafka container id={} running={} groupId={} topics={}",
                c.getListenerId(),
                c.isRunning(),
                c.getContainerProperties().getGroupId(),
                Arrays.toString(c.getContainerProperties().getTopics())
        ));
    }
}

