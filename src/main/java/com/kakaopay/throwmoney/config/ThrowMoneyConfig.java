package com.kakaopay.throwmoney.config;

import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.util.filter.IdentificationFilter;
import com.kakaopay.throwmoney.util.interceptor.IdentificationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;

@Configuration
public class ThrowMoneyConfig implements WebMvcConfigurer, WebMvcRegistrations {

    @Autowired
    private IdentificationInterceptor identificationInterceptor;

    /*public FilterRegistrationBean<IdentificationFilter> identificationFilter(@Autowired Identify identify) {
        FilterRegistrationBean<IdentificationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new IdentificationFilter(identify));
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/v1/*", "/v1/**"));
        return filterRegistrationBean;
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(identificationInterceptor)
                .addPathPatterns(Arrays.asList("/v1/*", "/v1/**"));
    }
}
