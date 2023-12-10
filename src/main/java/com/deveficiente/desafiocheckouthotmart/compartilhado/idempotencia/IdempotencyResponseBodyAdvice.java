package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class IdempotencyResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // Decida se o conselho deve ser aplicado com base no tipo de retorno e/ou no conversor
        return true; // Retornar true significa que o conselho será aplicado
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

    	
    	
    	
        return body; // Retornar o corpo da resposta modificado ou inalterado
    }
}
