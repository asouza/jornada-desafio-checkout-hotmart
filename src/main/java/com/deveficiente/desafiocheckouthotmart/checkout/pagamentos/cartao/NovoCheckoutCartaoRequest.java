package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao;

import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraCartao;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.InfoPadraoCheckoutRequest;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.TemInfoPadrao;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.NovoPagamentoGatewayCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class NovoCheckoutCartaoRequest implements TemInfoPadrao{

	@NotNull
	@Valid
	private InfoPadraoCheckoutRequest infoPadrao;
	@NotNull
	@Valid
	private DadosCartaoCreditoRequest dadosCartao;

	public NovoCheckoutCartaoRequest(InfoPadraoCheckoutRequest infoPadrao,
			DadosCartaoCreditoRequest dadosCartao) {
		super();
		this.infoPadrao = infoPadrao;
		this.dadosCartao = dadosCartao;
	}

	@Override
	public String toString() {
		return "NovoCheckoutCartaoRequest [infoPadrao=" + infoPadrao
				+ ", dadosCartao=" + dadosCartao + "]";
	}

	public InfoPadraoCheckoutRequest getInfoPadrao() {
		return infoPadrao;
	}

	public DadosCartaoCreditoRequest getDadosCartao() {
		return dadosCartao;
	}
	
	public void preencheInformacoesCartao(
			NovoPagamentoGatewayCartaoRequest novoPagamentoGatewayCartaoRequest,
			Oferta oferta) {
		
		dadosCartao.preencheInformacoesCartao(novoPagamentoGatewayCartaoRequest,oferta);
	}

	public InfoCompraCartao toInfoCompraCartao(Oferta oferta) {
		return dadosCartao.toInfoCompraCartao(oferta);
	}
}
