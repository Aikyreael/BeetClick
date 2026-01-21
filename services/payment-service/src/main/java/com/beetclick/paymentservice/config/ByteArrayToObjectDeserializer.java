package com.beetclick.paymentservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ByteArrayToObjectDeserializer<T> implements Deserializer<T> {

    private final Class<T> targetClass;
    private final ObjectMapper objectMapper;

    public ByteArrayToObjectDeserializer(Class<T> targetClass, ObjectMapper objectMapper) {
        this.targetClass = targetClass;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) return null;
        try {
            return objectMapper.readValue(data, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Kafka message", e);
        }
    }

    @Override
    public void close() {
    }
}
