package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class NovoCheckoutCartaoRequest {

	@NotNull
	@Valid
	private InfoPadraoCheckoutRequest infoPadrao;
	@NotNull
	@Valid
	private DadosCartaoCreditoRequest dadosCartao;

	public NovoCheckoutCartaoRequest(InfoPadraoCheckoutRequest infoPadrao,
			DadosCartaoCreditoRequest dadosCartao) {
		super();
		this.infoPadrao = infoPadrao;
		this.dadosCartao = dadosCartao;
	}

	@Override
	public String toString() {
		return "NovoCheckoutCartaoRequest [infoPadrao=" + infoPadrao
				+ ", dadosCartao=" + dadosCartao + "]";
	}

}
