package com.kakaopay.throwmoney.common.support.apiresponse;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(ApiResponseImportRegistrar.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public @interface EnableApiResponse {

    /**
     * ApiResponse 를 제외할 팩키지
     * @return 제외 팩키지
     */
    @Deprecated
    String[] excludes() default {};

    /**
     * ApiResponse 를 포함할 팩키지
     * @return 포함 팩키지
     */
    String[] includes() default {};

}
