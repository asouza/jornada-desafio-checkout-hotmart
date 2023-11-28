package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Optional;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

	public MetadadosCompra(Compra compra) {
		this.compra = compra;
	}

	public void setInfoCompraCartao(InfoCompraCartao infoCompraCartao) {
		this.infoCompraCartao = infoCompraCartao;		
	}
	
	public Optional<InfoCompraCartao> buscaInfoCompraCartao() {
		return Optional.ofNullable(infoCompraCartao);
	}

}
