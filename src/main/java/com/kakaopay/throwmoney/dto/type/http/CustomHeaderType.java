package com.kakaopay.throwmoney.dto.type.http;

import lombok.Getter;

@Getter
public enum CustomHeaderType {
    USER_ID("X-USER-ID"),
    ROOM_ID("X-ROOM-ID");

    private String value;

    CustomHeaderType(String value) {
        this.value = value;
    }

}
