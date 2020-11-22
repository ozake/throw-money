package com.kakaopay.throwmoney.dto.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThrowMoneyDto {
    private Long id;

    private String token;

    private Long roomId;

    private Long userId;

    private Integer MemberCnt;

    private Long amount;
}
