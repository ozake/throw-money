package com.kakaopay.throwmoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ThrowDetailsResponse {
    private LocalDateTime throwTime;
    private Long throwAmount;
    private Long receviedAmount;
    private List<ReceviedDetailsDto> receviedDetailsDtoList;
}
