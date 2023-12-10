package com.deveficiente.desafiocheckouthotmart.compartilhado.spring;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia.IdempotentInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IdempotentInterceptor());
    }
}
