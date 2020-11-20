package com.kakaopay.throwmoney;

import com.kakaopay.throwmoney.common.support.apiresponse.EnableApiResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableApiResponse
public class ThrowMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThrowMoneyApplication.class, args);
	}

}
