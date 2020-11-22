package com.kakaopay.throwmoney.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThrowResponse {
    private String token;
}
