package com.beetclick.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MatchResult {
    ONE("1"),
    DRAW("X"),
    TWO("2");

    private final String code;

    MatchResult(String code) { this.code = code; }

    @JsonValue
    public String code() { return code; }

    @JsonCreator
    public static MatchResult from(String value) {
        if (value == null) return null;
        return switch (value) {
            case "1" -> ONE;
            case "X", "x" -> DRAW;
            case "2" -> TWO;
            default -> throw new IllegalArgumentException("Invalid result: " + value);
        };
    }
}
