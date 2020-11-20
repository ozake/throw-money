package com.kakaopay.throwmoney;

import com.kakaopay.throwmoney.config.SwaggerUiWebMvcConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThrowMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThrowMoneyApplication.class, args);
	}

}
