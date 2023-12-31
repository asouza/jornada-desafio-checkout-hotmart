package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Optional;
import java.util.function.Function;

import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

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
	@Embedded
	private InfoCompraCartao infoCompraCartao;
	@Embedded
	private InfoCompraBoleto infoCompraBoleto;
	
	@Deprecated
	public MetadadosCompra() {
		// TODO Auto-generated constructor stub
	}

	public MetadadosCompra(Compra compra) {
		this.compra = compra;
	}

	public Optional<InfoCompraCartao> buscaInfoCompraCartao() {
		return Optional.ofNullable(infoCompraCartao);
	}
	
	public Optional<InfoCompraBoleto> buscaInfoCompraBoleto() {
		return Optional.ofNullable(infoCompraBoleto);
	}

	public void setInfoCompraCartao(Function<Oferta, InfoCompraCartao> funcaoCriadoraInfoCompraCartao) {
		this.infoCompraCartao = funcaoCriadoraInfoCompraCartao.apply(compra.getOferta());
	}

	public void setInfoBoleto(InfoCompraBoleto infoCompraBoleto) {
		this.infoCompraBoleto = infoCompraBoleto;
	}

}
