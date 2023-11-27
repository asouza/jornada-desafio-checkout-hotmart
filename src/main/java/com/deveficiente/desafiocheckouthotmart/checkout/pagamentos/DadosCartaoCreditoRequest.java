package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.deveficiente.desafiocheckouthotmart.compartilhado.FutureOrPresentYear;

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
	@FutureOrPresentYear
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
