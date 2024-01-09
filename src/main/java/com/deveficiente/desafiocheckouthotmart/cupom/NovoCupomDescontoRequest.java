package com.deveficiente.desafiocheckouthotmart.cupom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

public class NovoCupomDescontoRequest {

	@NotBlank
	private String codigo;
	@DecimalMin("1")
	@DecimalMax("99")
	private BigDecimal percentualDesconto;
	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy kk:mm")
	@Future
	private LocalDateTime limiteUso;

	public NovoCupomDescontoRequest(@NotBlank String codigo,
			@DecimalMin("0.01") @DecimalMax("99") BigDecimal percentualDesconto,
			LocalDateTime limite) {
		super();
		this.codigo = codigo;
		this.percentualDesconto = percentualDesconto;
		this.limiteUso = limite;
	}

	public Cupom toModel(Produto produto) {
		return new Cupom(produto, codigo, percentualDesconto,limiteUso);
	}

}
