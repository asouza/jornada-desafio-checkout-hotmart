package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.JsonHelper;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;

//@ControllerAdvice
public class IdempotencyResponseBodyAdvice implements ResponseBodyAdvice<Object> {
	
	@Autowired
	private HttpServletRequest httpServletRequest;
	@Autowired
	private ExecutaTransacao executaTransacao;
	@Autowired
	private IdempontecyValuePersister idempontecyValuePersister;
	
	
	private static final Logger log = LoggerFactory
			.getLogger(IdempotencyResponseBodyAdvice.class);


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {

        boolean isAPost = returnType.hasMethodAnnotation(PostMapping.class);
        Optional<String> needsIdempotency = Optional.ofNullable(httpServletRequest.getHeader("Idempotency-Key"));        
        
        Log5WBuilder	
        	.metodo()
        	.oQueEstaAcontecendo("Analyzing if it should apply the IdempotencyResponseAdvice")
        	.adicionaInformacao("isAPost", isAPost+"")
        	.adicionaInformacao("needsIdempotency", needsIdempotency+"")
        	.info(log);
        
        
		return isAPost && needsIdempotency.isPresent();
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

    	
    	
    	String idempotencyKey = httpServletRequest.getHeader("Idempotency-Key");
    	
    	executaTransacao.semRetorno(() -> {
    		idempontecyValuePersister.execute(idempotencyKey,body);
    	});
    	
        return body; // Retornar o corpo da resposta modificado ou inalterado
    }
}
