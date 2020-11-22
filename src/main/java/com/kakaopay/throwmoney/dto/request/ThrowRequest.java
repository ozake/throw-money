package com.kakaopay.throwmoney.dto.request;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ThrowRequest {

    @NotNull(message = "금액은 필수 값입니다.")
    @Min(value = 0, message = "0보다 커야합니다.")
    @ApiParam(required = true)
    private Long amount;

    @NotNull(message = "인원수는 필수값입니다.")
    @Min(value = 1, message = "인원수는 1명 이상이어야 합니다.")
    @ApiParam(required = true)
    private Integer memberCnt;
}
