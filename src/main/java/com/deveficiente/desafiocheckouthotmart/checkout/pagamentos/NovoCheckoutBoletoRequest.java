package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NovoCheckoutBoletoRequest {

	@NotNull
	@Valid
	private InfoPadraoCheckoutRequest infoPadrao;
	@NotNull
	@NotBlank
	@CPF
	private String cpf;

	public NovoCheckoutBoletoRequest(
			@NotNull @Valid InfoPadraoCheckoutRequest infoPadrao,
			@NotNull @NotBlank @CPF String cpf) {
		super();
		this.infoPadrao = infoPadrao;
		this.cpf = cpf;
	}


	public InfoPadraoCheckoutRequest getInfoPadrao() {
		return infoPadrao;
	}
	
	public String getCpf() {
		return cpf;
	}
}
