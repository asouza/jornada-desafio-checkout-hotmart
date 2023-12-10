package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.PagaComCartaoCreditoController.Retorno;
import com.deveficiente.desafiocheckouthotmart.compartilhado.JsonHelper;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
@Component
public class PostMappingAspect {

	@Autowired
	private HttpServletRequest httpServletRequest;
	@Autowired
	private IdempotencyKeyPairRepository idempotencyKeyPairRepository;
	
	private static final Logger log = LoggerFactory
			.getLogger(PostMappingAspect.class);


	@Around("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		
		Optional<String> needsIdempotency = Optional
				.ofNullable(httpServletRequest.getHeader("Idempotency-Key"));
		
		return 
		needsIdempotency
		.map(key -> {
			Optional<IdempotencyKeyPair> pair = idempotencyKeyPairRepository
					.findByIdempotencyKey(key);
			
			return pair;
		})
		.filter(pair -> pair.isPresent())
		.map(pair -> {
			
			Log5WBuilder
				.metodo("PostMappingAspect#execute")
				.oQueEstaAcontecendo("Returning idempotent value")
				.adicionaInformacao("idempontentKey", needsIdempotency.get())
				.info(log);
			
			//just to become compatible with proceed returning
			String json = pair.get().getIdempotencyValue();
			
			return (Object)JsonHelper.desserializa(json, Retorno.class);
		})
		.orElseGet(() -> {
			
			Log5WBuilder
			.metodo("PostMappingAspect#execute")
			.oQueEstaAcontecendo("Let the request flows")
			.adicionaInformacao("idempontentKey", needsIdempotency.get())
			.info(log);
			
			try {
				return joinPoint.proceed();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}
}
