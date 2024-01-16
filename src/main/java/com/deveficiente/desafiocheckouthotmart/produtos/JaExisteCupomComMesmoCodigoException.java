package com.deveficiente.desafiocheckouthotmart.produtos;

import com.deveficiente.desafiocheckouthotmart.cupom.Cupom;

public class JaExisteCupomComMesmoCodigoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cupom cupom;

	public JaExisteCupomComMesmoCodigoException(Cupom novoCupom) {
		this.cupom = novoCupom;
	}

	public String getCodigoCupom() {
		return cupom.getCodigo();
	}

}
