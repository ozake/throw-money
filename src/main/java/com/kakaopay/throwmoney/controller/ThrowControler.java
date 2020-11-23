package com.kakaopay.throwmoney.controller;

import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.request.TokenRequest;
import com.kakaopay.throwmoney.dto.response.ThrowDetailsResponse;
import com.kakaopay.throwmoney.dto.response.ThrowResponse;
import com.kakaopay.throwmoney.service.ThrowMoneyApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@RestController
@Api(value = "뿌리기 컨트롤러")
@Slf4j
@RequestMapping(value = "/v1/throw")
public class ThrowControler {

    @Autowired
    private ThrowMoneyApiService throwMoneyApiService;

    @PostMapping(consumes = "application/json")
    @ApiOperation(value = "뿌리기 요청")
    public ThrowResponse throwMoney(@RequestBody @Valid ThrowRequest throwRequest) {
        return throwMoneyApiService.throwMoney(throwRequest);
    }

    @GetMapping
    @ApiOperation(value = "뿌린 내역 조회 ")
    public ThrowDetailsResponse getThrowDetails(@RequestParam
                                                    @Valid
                                                    @Size(max = 3, min = 3, message = "토큰의 길이는 3글자 입니다.")
                                                            String token) {
        return throwMoneyApiService.getThrowDetails(token);
    }

}
