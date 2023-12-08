package com.deveficiente.desafiocheckouthotmart.compartilhado.spring;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Component
public class ConfiguraPoliticaCircuitBreak {

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;
	
	@Bean("circuitBreakerCartao")
	public CircuitBreaker configuraCartao() {
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
				  .failureRateThreshold(50)
				  .slowCallRateThreshold(50)
				  .waitDurationInOpenState(Duration.ofMillis(60000))
				  .slowCallDurationThreshold(Duration.ofMillis(500))
				  .permittedNumberOfCallsInHalfOpenState(4)
				  .minimumNumberOfCalls(10)
				  .slidingWindowType(SlidingWindowType.COUNT_BASED)
				  .slidingWindowSize(2)
				  .recordException(e -> {
					  if(e instanceof FeignException) {
						  FeignException feignException = (FeignException) e;
						  return feignException.status() >= 500;
					  }
					  
					  return false;
				  })
				  .recordExceptions(TimeoutException.class)				  
				  .build();
		
		 
		return circuitBreakerRegistry.circuitBreaker("gatewayCartao",circuitBreakerConfig);

	}
}
