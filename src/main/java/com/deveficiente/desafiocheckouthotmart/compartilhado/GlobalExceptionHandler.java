package com.deveficiente.desafiocheckouthotmart.compartilhado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	
	private static final Logger log = LoggerFactory
			.getLogger(GlobalExceptionHandler.class);


	@ExceptionHandler(CallNotPermittedException.class)
	public ResponseEntity<String> handleCallNotPermittedException(
			CallNotPermittedException ex) {
		// Lógica para lidar com a exceção
		
		Log5WBuilder
			.metodo()
			.oQueEstaAcontecendo("Algum circuit breaker chegou no limite")
			.adicionaInformacao("nome", ex.getCausingCircuitBreakerName())
			.info(log);
		
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Estamos com problemas de comunicacao com parceiros");
	}

}
