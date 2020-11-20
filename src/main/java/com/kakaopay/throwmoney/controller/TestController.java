package com.kakaopay.throwmoney.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/test")
public class TestController {

    @GetMapping("/hello")
    public String test() {
        return "hello";
    }
}
