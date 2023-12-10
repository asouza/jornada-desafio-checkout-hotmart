package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class ExecutaTransacao {

	@Transactional
	public <T> T comRetorno(Supplier<T> supplier) {
		return supplier.get();
	}

	@Transactional
	public void semRetorno(Runnable runnable) {
		runnable.run();
	}
}
