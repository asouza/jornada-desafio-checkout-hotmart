package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class OptionalToHttpStatusException {

	/**
	 * 
	 * @param <T>
	 * @param optionalObject
	 * @param status
	 * @param exceptionMessage
	 * @return optional unwraped 
	 * @throws ResponseStatusException when Optional is empty
	 */
	public static <T> T execute(Optional<T> optionalObject, int status,
			String exceptionMessage) {
		
		return optionalObject.orElseThrow(() -> {
			return new ResponseStatusException(HttpStatusCode.valueOf(status),exceptionMessage);
		});
	}

}
