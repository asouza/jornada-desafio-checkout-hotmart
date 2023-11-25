package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

public class ValorParcelaMesResponse {

	private BigDecimal valor;
	private int numeroParcelas;

	public ValorParcelaMesResponse(ValorParcelaMes valorParcelaMes) {
		this.valor = valorParcelaMes.getValor();
		this.numeroParcelas = valorParcelaMes.getNumeroParcelas();
	}
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public int getNumeroParcelas() {
		return numeroParcelas;
	}
}
