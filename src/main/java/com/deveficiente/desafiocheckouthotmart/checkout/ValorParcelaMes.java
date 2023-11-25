package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

public class ValorParcelaMes {

	private BigDecimal valor;
	private int numeroParcelas;

	public ValorParcelaMes(BigDecimal valor, int numeroParcelas) {
		super();
		this.valor = valor;
		this.numeroParcelas = numeroParcelas;
	}

	public BigDecimal getValor() {
		return valor;
	}
	
	public int getNumeroParcelas() {
		return numeroParcelas;
	}

}
