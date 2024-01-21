package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.validator.constraints.UUID;
import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InfoCompraBoleto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CPF
	@NotBlank
	private String cpf;
	@UUID
	@NotBlank
	private String codigoBoleto;
	@Positive
	@NotNull
	private BigDecimal valor;
	@FutureOrPresent
	@NotNull
	private LocalDate dataExpiracao;

	@Deprecated
	public InfoCompraBoleto() {
		// TODO Auto-generated constructor stub
	}

	public InfoCompraBoleto(@NotBlank @CPF String cpf,
			@NotBlank @UUID String codigoBoleto,
			@NotNull @Min(1) BigDecimal valor,
			@FutureOrPresent LocalDate dataExpiracao) {
		super();
		this.cpf = cpf;
		this.codigoBoleto = codigoBoleto;
		this.valor = valor;
		this.dataExpiracao = dataExpiracao;
	}

	public String getCpf() {
		return cpf;
	}

	public String getCodigoBoleto() {
		return codigoBoleto;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public LocalDate getDataExpiracao() {
		return dataExpiracao;
	}

}
