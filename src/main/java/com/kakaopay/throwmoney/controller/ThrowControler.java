package com.kakaopay.throwmoney.controller;

import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.response.ThrowResponse;
import com.kakaopay.throwmoney.service.ThrowMoneyApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(value = "뿌리기 컨트롤러")
@Slf4j
public class ThrowControler {

    @Autowired
    private ThrowMoneyApiService throwMoneyApiService;

    @PostMapping(value = "/v1/throw", consumes = "application/json")
    @ApiOperation(value = "뿌리기 요청")
    public ThrowResponse throwMoney(@RequestBody @Valid ThrowRequest throwRequest) {

        return ThrowResponse.builder().token(throwMoneyApiService.generateToken()).build();
    }
}
