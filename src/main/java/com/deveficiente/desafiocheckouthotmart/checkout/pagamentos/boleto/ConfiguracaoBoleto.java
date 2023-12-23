package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoBoleto {

	
	public LocalDate dataExpiracao(LocalDate dataOrigem) {
		return dataOrigem.plusDays(3);
	}
	
	public String geraCodigoParaBoleto() {
		return UUID.randomUUID().toString();
	}
}
