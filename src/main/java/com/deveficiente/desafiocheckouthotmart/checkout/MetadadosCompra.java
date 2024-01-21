package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Aqui representa os possíveis metadados de uma compra. As possibilidades são:
 * <ul>
 *  <li>Cartao credito</li>
 *  <li>Boleto</li>
 * </ul>
 * @author albertoluizsouza
 *
 */
@Entity
public class MetadadosCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	private Compra compra;
	@OneToOne(mappedBy = "metadadosCompra")
	@Valid
	private InfoCompraCartao infoCompraCartao;
	@OneToOne(mappedBy = "metadadosCompra")
	@Valid
	private InfoCompraBoleto infoCompraBoleto;
	
	
	@Deprecated
	public MetadadosCompra() {
		// TODO Auto-generated constructor stub
	}

	public MetadadosCompra(Compra compra,InfoCompraCartao infoCompraCartao) {
		this.compra = compra;
		this.infoCompraCartao = infoCompraCartao;
	}
	
	public MetadadosCompra(Compra compra,InfoCompraBoleto infoCompraBoleto) {
		this.compra = compra;
		this.infoCompraBoleto = infoCompraBoleto;
	}

	public Optional<InfoCompraCartao> buscaInfoCompraCartao() {
		return Optional.ofNullable(infoCompraCartao);
	}
	
	public Optional<InfoCompraBoleto> buscaInfoCompraBoleto() {
		return Optional.ofNullable(infoCompraBoleto);
	}

	public @NotNull BigDecimal calculaPossivelDescontoRepasse(
			QuemPagaJuros quemPagaJuros,BigDecimal valor,Configuracao configuracao) {
		
		if(infoCompraCartao != null) {
			return infoCompraCartao.calculaPossivelDescontoRepasse(quemPagaJuros,valor,configuracao);
		}
		
		return valor;
	}

}
