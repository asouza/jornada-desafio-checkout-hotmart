package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IdempotentInterceptor implements HandlerInterceptor {
	
	
	private static final Logger log = LoggerFactory
			.getLogger(IdempotentInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	
    	if(!request.getMethod().equals("POST")) {
    		//aqui deveria ser debug
    		Log5WBuilder
			.metodo("IdempotentInterceptor#preHandler")
			.oQueEstaAcontecendo("Descartando request que não é post")
			.adicionaInformacao("method", request.getMethod())
			.info(log);    		
    	}
    		
    	
    	Optional<String> needsIdempotency = Optional.ofNullable(request.getHeader("Idempotency-Key"));
    	
    	return needsIdempotency.map(key -> {
    		//aqui seria debug
    		Log5WBuilder
    			.metodo("IdempotentInterceptor#preHandler")
    			.oQueEstaAcontecendo("Verificando se já existe chave idempotencia")
    			.adicionaInformacao("key", key)
    			.info(log);
    		
    		//verifica se a chave já exis
    		
    		return true;    		
    	}).orElse(true);
    	
    }

}
