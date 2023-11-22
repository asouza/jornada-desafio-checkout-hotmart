package com.deveficiente.desafiocheckouthotmart.compartilhado;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;

//TODO tests
public class BindExceptionFactory {

	/**
	 * 
	 * @param request object which represents request data
	 * @param errorName name for the {@link BindingResult}
	 * @param message global message
	 * @return New {@link BindException}
	 */
	public static BindException createGlobalError(@Valid Object request,
			String errorName, String message) {
    	BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, errorName);
    	bindingResult.reject(null, message);
    	
    	return new BindException(bindingResult);
		
	}

}
