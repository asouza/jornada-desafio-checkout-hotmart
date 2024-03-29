package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class RemoteHttpClient {

	/**
	 * 
	 * @param <T>
	 * @param integracao
	 * @return
	 * @throws Erro500Exception,Erro400Exception
	 */
	public <T> Result<RuntimeException, T> execute(Supplier<T> integracao) {
		try {
			return Result.successWithReturn(integracao.get());
		} catch (FeignException e) {			
			if(e.status() >= 500) {
				return Result.failWithProblem(new Erro500Exception(e));
			}
			
			//quando falha com timeout, vem status -1
			if(e.status() >= 400) {
				return Result.failWithProblem(new Erro400Exception(e));				
			}
			
			return Result.failWithProblem(e);
			
		}
	}

}
