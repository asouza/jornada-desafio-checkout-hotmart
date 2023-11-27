package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.hibernate.validator.constraints.CreditCardNumber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DadosCartaoCreditoRequest {

	@NotBlank
	@CreditCardNumber
	private String numeroCartao;
	@NotBlank
	private String nomeTitular;
	@NotNull
	private MesVencimentoCartao mes;
	@Positive
	// aqui pode valer criar uma annotation customizada para validar se está
	// presente ou no futuro
	private int anoVencimento;

	public DadosCartaoCreditoRequest(
			@NotBlank @CreditCardNumber String numeroCartao,
			@NotBlank String nomeTitular, @NotNull MesVencimentoCartao mes,
			@Positive int anoVencimento) {
		super();
		this.numeroCartao = numeroCartao;
		this.nomeTitular = nomeTitular;
		this.mes = mes;
		this.anoVencimento = anoVencimento;
	}

	@Override
	public String toString() {
		// aqui não poderia logar
		return "DadosCartaoCreditoRequest [numeroCartao=" + numeroCartao
				+ ", nomeTitular=" + nomeTitular + ", mes=" + mes
				+ ", anoVencimento=" + anoVencimento + "]";
	}

}
