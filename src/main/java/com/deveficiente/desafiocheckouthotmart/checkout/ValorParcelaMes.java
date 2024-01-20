package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Embeddable
public class ValorParcelaMes {

	private BigDecimal valor;
	private int numeroParcelas;
	
	@Deprecated
	public ValorParcelaMes() {
		// TODO Auto-generated constructor stub
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numeroParcelas;
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValorParcelaMes other = (ValorParcelaMes) obj;
		if (numeroParcelas != other.numeroParcelas)
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

	public boolean numeroVezesIgual(@Positive @NotNull int numeroParcelasNecessario) {
		return this.numeroParcelas == numeroParcelasNecessario;
	}

	/**
	 * 
	 * @param outro
	 * @return respeita o contrato do Comparable aqui. 0 é igual, negativo este é menor e positivo o outor é maior
	 */
	public int comparaParcelas(ValorParcelaMes outro) {
		return this.numeroParcelas - outro.numeroParcelas;
	}
	
	

}
