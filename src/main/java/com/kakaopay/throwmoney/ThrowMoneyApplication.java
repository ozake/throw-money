package com.kakaopay.throwmoney;

import com.kakaopay.throwmoney.common.support.apiresponse.EnableApiResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EnableApiResponse
public class ThrowMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThrowMoneyApplication.class, args);
	}

}
