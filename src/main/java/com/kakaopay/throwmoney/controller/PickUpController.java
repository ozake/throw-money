package com.kakaopay.throwmoney.controller;

import com.kakaopay.throwmoney.dto.request.TokenRequest;
import com.kakaopay.throwmoney.dto.response.PickUpResponse;
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
@RequestMapping(value = "/v1/pickup")
public class PickUpController {

    @Autowired
    private ThrowMoneyApiService throwMoneyApiService;

    @PostMapping(consumes = "application/json")
    @ApiOperation(value = "받기 요청")
    public PickUpResponse pickUpMoney(@RequestBody @Valid TokenRequest tokenRequest) {
        return throwMoneyApiService.pickUpMoney(tokenRequest);
    }
}
