package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao.PagaComCartaoCreditoController.Retorno;
import com.deveficiente.desafiocheckouthotmart.compartilhado.JsonHelper;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
@Component
public class PostMappingAspect {

	@Autowired
	private HttpServletRequest httpServletRequest;
	@Autowired
	private HttpServletResponse httpServletResponse;
	@Autowired
	private IdempotencyValueFinder idempotencyValueFinder;
	
	private static final Logger log = LoggerFactory
			.getLogger(PostMappingAspect.class);


	@Around("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

		String idempotencyKey = httpServletRequest.getHeader("Idempotency-Key");
		
		if(!StringUtils.hasText(idempotencyKey)) {
			return joinPoint.proceed();
		}
		
		return  
				
		idempotencyValueFinder.execute(idempotencyKey)					
		.map(pair -> {			
			Log5WBuilder
				.metodo("PostMappingAspect#execute")
				.oQueEstaAcontecendo("Returning idempotent value")
				.adicionaInformacao("idempontentKey", idempotencyKey)
				.info(log);		
			
			httpServletResponse.setHeader("idempotent-response", "true");
			return pair;
		})
		.orElseGet(() -> {
			
			Log5WBuilder
				.metodo("PostMappingAspect#execute")
				.oQueEstaAcontecendo("Let the request flows")
				.adicionaInformacao("idempontentKey", idempotencyKey)
				.info(log);
			
			try {
				return joinPoint.proceed();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}
}
