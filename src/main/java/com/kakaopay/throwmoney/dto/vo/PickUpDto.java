package com.kakaopay.throwmoney.dto.vo;

import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickUpDto {
    private Long id;

    private Long throwMoneyId;

    private Long userId;

    private Long amount;

    private YesNoType isReceived;
}
