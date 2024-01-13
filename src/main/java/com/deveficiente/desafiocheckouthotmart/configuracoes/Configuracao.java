package com.deveficiente.desafiocheckouthotmart.configuracoes;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.checkout.TransacaoCompra;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Configuracao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private @Positive @NotNull BigDecimal taxaComissao;
	private @Positive @NotNull BigDecimal taxaJuros;
	private @NotNull boolean opcaoDefault;

	@Deprecated
	Configuracao() {

	}

	public Configuracao(@Positive @NotNull BigDecimal taxaComissao,
			@Positive @NotNull BigDecimal taxaJuros,
			@NotNull Boolean opcaoDefault) {

		// #copilotGerou
		Assert.isTrue(taxaComissao.compareTo(BigDecimal.ZERO) > 0,
				"A taxa de comissao precisa ser maior que zero");
		// #copilotGerou
		Assert.isTrue(taxaJuros.compareTo(BigDecimal.ZERO) > 0,
				"A taxa de juros precisa ser maior que zero");
		// #copilotGerou
		Assert.notNull(opcaoDefault, "A opcao default nao pode ser nula");

		this.taxaComissao = taxaComissao;
		this.taxaJuros = taxaJuros;
		this.opcaoDefault = opcaoDefault;
	}

	public BigDecimal getTaxaJuros() {
		return this.taxaJuros;
	}

	public LocalDate calculaDiaPagamento(TransacaoCompra tx) {
		// aqui poderia receber uma data e pronto
//		return tx.getInstante().plusDays(30).toLocalDate();
		
		//essa versão segue o algoritmo de deixar operacao sobre atributo dentro da classe
		return tx.somaDiasAoInstante(30).toLocalDate();
	}

	public boolean isDefault() {
		return opcaoDefault;
	}

	public BigDecimal calculaComissao(@NotNull BigDecimal precoFinal) {
		BigDecimal percentualComissao = this.taxaComissao.divide(new BigDecimal(100));
		return precoFinal.multiply(percentualComissao);
	}

}
