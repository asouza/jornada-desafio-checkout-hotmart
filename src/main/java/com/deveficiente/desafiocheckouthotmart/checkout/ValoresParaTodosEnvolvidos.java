package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Guarda os valores que precisam ser distribuídos entre as partes
 * envolvidas na transação.
 * @author albertoluizsouza
 *
 */
@Embeddable
public class ValoresParaTodosEnvolvidos {

	@NotNull
	@DecimalMin("0")
	private BigDecimal valorComissao;
	@NotNull
	@DecimalMin("0")
	private BigDecimal valorDeRepasse;
	@NotNull
	@DecimalMin("0")
	private BigDecimal descontoRepassePorTaxasExtras;
	
	@Deprecated
	public ValoresParaTodosEnvolvidos() {
		// TODO Auto-generated constructor stub
	}

	public ValoresParaTodosEnvolvidos(BigDecimal valorComissao,
			BigDecimal valorDeRepasse, BigDecimal descontoRepassePorTaxasExtras) {
				this.valorComissao = valorComissao;
				this.valorDeRepasse = valorDeRepasse;
				this.descontoRepassePorTaxasExtras = descontoRepassePorTaxasExtras;
	}

}
