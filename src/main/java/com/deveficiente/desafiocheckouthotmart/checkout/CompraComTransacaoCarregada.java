package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.UUID;

public class CompraComTransacaoCarregada {

	private Compra compra;

	public CompraComTransacaoCarregada(Compra compra) {
		super();
		this.compra = compra;
	}

	public Provisionamento calculaProvisionamento() {
		return compra.calculaProvisionamento();
	}

	public void provisionouOPagamento() {
		compra.provisionouOPagamento();
	}

	public UUID getCodigo() {
		return compra.getCodigo();
	}

	public Compra getCompra() {
		return compra;
	}

}
