package com.kakaopay.throwmoney.common.support.Identification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Identify {
    private Long UserId;
    private Long RoomId;
}
