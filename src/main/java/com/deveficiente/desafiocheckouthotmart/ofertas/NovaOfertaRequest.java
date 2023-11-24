package com.deveficiente.desafiocheckouthotmart.ofertas;

import java.math.BigDecimal;

import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NovaOfertaRequest {

	@NotBlank
	private String nome;

	@NotNull
	@Positive
	private BigDecimal preco;

	@NotNull
	@Min(1)
	@Max(12)
	private Integer numeroMaximoParcelas;

	@NotNull
	private QuemPagaJuros quemPagaJuros;

	public NovaOfertaRequest(@NotBlank String nome,
			@NotNull @Positive BigDecimal preco,
			@NotNull @Min(1) @Max(12) Integer numeroMaximoParcelas,
			@NotNull QuemPagaJuros quemPagaJuros) {
		super();
		this.nome = nome;
		this.preco = preco;
		this.numeroMaximoParcelas = numeroMaximoParcelas;
		this.quemPagaJuros = quemPagaJuros;
	}

	public Oferta toModel(Produto produto) {
		return new Oferta(produto, nome, preco, numeroMaximoParcelas, quemPagaJuros);
	}

}
