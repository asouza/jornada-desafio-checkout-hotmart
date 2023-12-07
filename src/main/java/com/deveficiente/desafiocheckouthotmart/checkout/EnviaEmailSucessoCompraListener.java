package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EnviaEmailSucessoCompraListener {

	private static final Logger log = LoggerFactory
			.getLogger(EnviaEmailSucessoCompraListener.class);

	@JmsListener(destination = "envia-email-sucesso-compra", containerFactory = "myFactory")
	
	public void receiveMessage(Map<String, String> mensagem) {
		System.out.println(mensagem);
	}
}
