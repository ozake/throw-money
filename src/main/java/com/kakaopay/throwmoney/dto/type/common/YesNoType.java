package com.kakaopay.throwmoney.dto.type.common;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum YesNoType {
    YES("Y"),
    NO("N");

    private String value;

    YesNoType(String value) {
        this.value = value;
    }

    public static YesNoType of(String value) {
        return Stream.of(YesNoType.values())
                .filter(yesNoType -> yesNoType.getValue().equals(value))
                .findFirst()
                .orElse(NO);
    }
}
