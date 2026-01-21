package com.beetclick.matchservice.entity;

import com.beetclick.common.entity.MatchResult;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MatchResultConverter implements AttributeConverter<MatchResult, String> {
    @Override public String convertToDatabaseColumn(MatchResult attribute) {
        return attribute == null ? null : attribute.code();
    }
    @Override public MatchResult convertToEntityAttribute(String dbData) {
        return MatchResult.from(dbData);
    }
}
