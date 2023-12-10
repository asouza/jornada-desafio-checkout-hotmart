package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.aspectj.lang.JoinPoint;

@Aspect
@Component
public class PostMappingAspect {

	@Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
	public void beforePostMapping(JoinPoint joinPoint) {
		System.out.println("=====Before method: " + joinPoint.getSignature());
	}
}
