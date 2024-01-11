package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Provisionamento {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Conta conta;
	@NotNull
	private BigDecimal precoFinal;
	@NotNull
	private LocalDate dataLiberacaoPagamento;
	@NotNull
	private UUID codigoOferta;
	@NotNull
	private UUID codigoProduto;
	@NotNull
	private BigDecimal precoOriginal;

	public Provisionamento(Conta conta, UUID codigoProduto,UUID codigoOferta, BigDecimal precoOriginal ,BigDecimal precoFinal,
			LocalDate dataLiberacaoPagamento) {
				this.conta = conta;
				this.codigoOferta = codigoOferta;
				this.codigoProduto = codigoProduto;
				this.precoFinal = precoFinal;
				this.precoOriginal = precoOriginal;
				this.dataLiberacaoPagamento = dataLiberacaoPagamento;
	}

}
