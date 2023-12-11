package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

@Component
public class IdempontecyValuePersister {

	private IdempotencyKeyPairRepository idempotencyKeyPairRepository;
	
	private static final Logger log = LoggerFactory
			.getLogger(IdempontecyValuePersister.class);


	public IdempontecyValuePersister(
			IdempotencyKeyPairRepository idempotencyKeyPairRepository) {
		super();
		this.idempotencyKeyPairRepository = idempotencyKeyPairRepository;
	}

	public void execute(String idempotencyKey, Object body) {
		idempotencyKeyPairRepository
		.findByIdempotencyKey(idempotencyKey)
		.map(keyPair -> {
			
			Log5WBuilder
			.metodo("execute")
			.oQueEstaAcontecendo("Logging the existence of a idempontencyKey")
			.adicionaInformacao("idempotencyKey", idempotencyKey)
			.info(log);    				
			return keyPair;
		})
		.orElseGet(() -> {
			    				
			Log5WBuilder
				.metodo("execute")
				.oQueEstaAcontecendo("Saving new IdempotencyKeyPair")
				.adicionaInformacao("idempotencyKey", idempotencyKey)
				.info(log);    				    		    	
	    	
	    	IdempotencyKeyPair keyPair = new IdempotencyKeyPair(idempotencyKey,body);    				
			
			return idempotencyKeyPairRepository.save(keyPair);    				
		});
	}

}
