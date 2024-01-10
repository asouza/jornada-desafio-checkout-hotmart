package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.RegistraNovaContaService;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

@Component
public class CriaOBasicoDaCompraParaFluxosWeb {

	private ExecutaTransacao executaTransacao;
	private RegistraNovaContaService registraNovaContaService;
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;

	public CriaOBasicoDaCompraParaFluxosWeb(ExecutaTransacao executaTransacao,
			RegistraNovaContaService registraNovaContaService,
			BuscasNecessariasParaPagamento buscasNecessariasParaPagamento) {
		super();
		this.executaTransacao = executaTransacao;
		this.registraNovaContaService = registraNovaContaService;
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
	}

	public CompraBuilderPasso2 executa(InfoPadraoCheckoutRequest infoPadrao,
			String codigoProduto, String codigoOferta) {

		Conta conta = executaTransacao.comRetorno(() -> {
			return registraNovaContaService.executa(infoPadrao.getEmail());

		});

		@ICP
		Produto produto = OptionalToHttpStatusException
				.execute(buscasNecessariasParaPagamento.buscaProdutoPorCodigo(
						codigoProduto), 404, "Produto não encontrado");

		@ICP
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		CompraBuilderPasso2 basicoDaCompra = CompraBuilder.nova(conta, oferta);

		return basicoDaCompra;
	}

}
