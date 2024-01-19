package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import org.hibernate.validator.constraints.br.CPF;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.InfoPadraoCheckoutRequest;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.TemInfoPadrao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NovoCheckoutBoletoRequest implements TemInfoPadrao {

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
