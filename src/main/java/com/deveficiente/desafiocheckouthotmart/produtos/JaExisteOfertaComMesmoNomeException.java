package com.deveficiente.desafiocheckouthotmart.produtos;

import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

public class JaExisteOfertaComMesmoNomeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Oferta oferta;

	public JaExisteOfertaComMesmoNomeException(Oferta oferta) {
		this.oferta = oferta;
		// TODO Auto-generated constructor stub
	}

	public Oferta getOferta() {
		return this.oferta;
	}

}
