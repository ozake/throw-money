package com.kakaopay.throwmoney.controller;

import com.kakaopay.throwmoney.common.exception.ApiException;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/test")
@Api(value = "테스트컨트롤러")
public class TestController {

    @GetMapping("/hello")
    public List<String> test() {
        List<String> testArr = new ArrayList<>();
        testArr.add("hello");
        testArr.add("world");
        return testArr;
    }

    @GetMapping("/error")
    public void errorTest() {
        throw new ApiException("에러테스트", HttpStatus.BAD_REQUEST, 400);
    }
}
