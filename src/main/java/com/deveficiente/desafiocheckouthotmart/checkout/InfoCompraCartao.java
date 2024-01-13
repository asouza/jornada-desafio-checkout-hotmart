package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.deveficiente.desafiocheckouthotmart.compartilhado.FutureOrPresentYear;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Embeddable
public class InfoCompraCartao {

	@CreditCardNumber
	private String numeroCartao;
	private String nomeTitular;
	private BigDecimal valorParcela;
	@Positive
	private Integer numeroParcelas;
	@FutureOrPresentYear
	private Integer anoVencimento;
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

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public String getNomeTitular() {
		return nomeTitular;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public int getNumeroParcelas() {
		return numeroParcelas;
	}

	public int getAnoVencimento() {
		return anoVencimento;
	}

	public MesVencimentoCartao getMes() {
		return mes;
	}

	public @NotNull BigDecimal calculaPossivelDescontoRepasse(QuemPagaJuros quemPagaJuros,BigDecimal valor,Configuracao configuracao) {
		return quemPagaJuros.calculaPossivelDescontoDeRepasse(valor,numeroParcelas,configuracao.getTaxaJuros());
	}
	
	

}
