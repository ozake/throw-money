package com.kakaopay.throwmoney.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class TokenRequest {
    @Size(max = 3, min = 3, message = "토큰의 길이는 3글자 입니다.")
    private String token;
}
