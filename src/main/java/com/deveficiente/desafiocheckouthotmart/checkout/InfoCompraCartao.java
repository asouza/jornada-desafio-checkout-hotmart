package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.deveficiente.desafiocheckouthotmart.compartilhado.FutureOrPresentYear;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Embeddable
public class InfoCompraCartao {

	@CreditCardNumber
	@NotBlank
	private String numeroCartao;
	@NotBlank	
	private String nomeTitular;
	@NotNull
	private BigDecimal valorParcela;
	@Positive
	private int numeroParcelas;
	@FutureOrPresentYear
	private int anoVencimento;
	@Enumerated(EnumType.STRING)
	private MesVencimentoCartao mes;
	
	@Deprecated
	public InfoCompraCartao() {
		// TODO Auto-generated constructor stub
	}

	public InfoCompraCartao(String numeroCartao, String nomeTitular,
			BigDecimal valorParcela, int numeroParcelas, int anoVencimento,
			String mes) {
				this.numeroCartao = numeroCartao;
				this.nomeTitular = nomeTitular;
				this.valorParcela = valorParcela;
				this.numeroParcelas = numeroParcelas;
				this.anoVencimento = anoVencimento;
				this.mes = MesVencimentoCartao.from(mes);
	}

}
