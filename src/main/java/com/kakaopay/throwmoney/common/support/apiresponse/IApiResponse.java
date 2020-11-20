package com.kakaopay.throwmoney.common.support.apiresponse;

public interface IApiResponse {

    org.springframework.http.HttpStatus httpStatus();

    org.springframework.http.HttpHeaders headers();
}
