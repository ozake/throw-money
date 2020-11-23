package com.kakaopay.throwmoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReceviedDetailsDto {
    private Long userId;
    private Long amount;
}
