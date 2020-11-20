package com.kakaopay.throwmoney.config;

import com.kakaopay.throwmoney.dto.type.http.CustomHeaderType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Kakaopay throw-money API",
                "API Documentation for Kakaopay throw-money",
                "1.0",
                null, null, null, null )
                ;
    }

    @Bean
    public Docket api() {
        List<Parameter> parameterList = new ArrayList<>();
        Parameter userIdParmeater = new ParameterBuilder()
                .name(CustomHeaderType.USER_ID.getValue())
                .description(CustomHeaderType.USER_ID.name())
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build();
        parameterList.add(userIdParmeater);
        Parameter roomIdParmeater = new ParameterBuilder()
                .name(CustomHeaderType.ROOM_ID.getValue())
                .description(CustomHeaderType.ROOM_ID.name())
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build();
        parameterList.add(roomIdParmeater);
        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(parameterList)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(Timestamp.class, Long.class);
    }
}
