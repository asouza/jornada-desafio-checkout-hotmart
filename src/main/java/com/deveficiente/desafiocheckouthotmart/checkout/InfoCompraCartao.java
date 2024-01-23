package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.deveficiente.desafiocheckouthotmart.compartilhado.FutureOrPresentYear;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class InfoCompraCartao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreditCardNumber
	@NotBlank
	private String numeroCartao;
	@NotBlank
	private String nomeTitular;
	@NotNull
	@Positive
	private BigDecimal valorParcela;
	@NotNull
	@Positive
	private Integer numeroParcelas;
	@FutureOrPresentYear
	@NotNull
	private Integer anoVencimento;
	@Enumerated(EnumType.STRING)
	@NotNull
	private MesVencimentoCartao mes;
	@OneToOne
	private MetadadosCompra metadadosCompra;
	
	@Deprecated
	public InfoCompraCartao() {
		// TODO Auto-generated constructor stub
	}

	public InfoCompraCartao(String numeroCartao, String nomeTitular,
			BigDecimal valorParcela, int numeroParcelas, int anoVencimento,
			String mes,MetadadosCompra metadadosCompra) {
				this.numeroCartao = numeroCartao;
				this.nomeTitular = nomeTitular;
				this.valorParcela = valorParcela;
				this.numeroParcelas = numeroParcelas;
				this.anoVencimento = anoVencimento;
				this.metadadosCompra = metadadosCompra;
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
