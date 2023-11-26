package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.util.List;

import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

public class CheckoutDetalheOfertaResponse {

	private BigDecimal precoAVista;
	private String nomeProduto;
	private List<ValorParcelaMesResponse> descricaoParcelas;

	public CheckoutDetalheOfertaResponse(Oferta oferta) {
		this.precoAVista = oferta.getPreco();
		this.nomeProduto = oferta.getProduto().getNome();

		this.descricaoParcelas = oferta.getParcelaMes()
			.stream()
			.map(ValorParcelaMesResponse :: new)
			.toList();
	}
	
	public BigDecimal getPrecoAVista() {
		return precoAVista;
	}
	
	public String getNomeProduto() {
		return nomeProduto;
	}
	
	public List<ValorParcelaMesResponse> getDescricaoParcelas() {
		return descricaoParcelas;
	}

}
