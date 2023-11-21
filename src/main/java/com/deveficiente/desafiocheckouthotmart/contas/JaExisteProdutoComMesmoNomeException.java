package com.deveficiente.desafiocheckouthotmart.contas;

import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

public class JaExisteProdutoComMesmoNomeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Produto produto;

	public JaExisteProdutoComMesmoNomeException(Produto produto) {
		this.produto = produto;
	}
	
}
