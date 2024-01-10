package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso3;
import com.deveficiente.desafiocheckouthotmart.checkout.RegistraNovaContaService;
import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.cupom.Cupom;
import com.deveficiente.desafiocheckouthotmart.cupom.CupomRepository;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

@Component
public class CriaOBasicoDaCompraParaFluxosWeb {

	private ExecutaTransacao executaTransacao;
	private RegistraNovaContaService registraNovaContaService;
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	private CupomRepository cupomRepository;

	public CriaOBasicoDaCompraParaFluxosWeb(ExecutaTransacao executaTransacao,
			RegistraNovaContaService registraNovaContaService,
			BuscasNecessariasParaPagamento buscasNecessariasParaPagamento,
			CupomRepository cupomRepository) {
		super();
		this.executaTransacao = executaTransacao;
		this.registraNovaContaService = registraNovaContaService;
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
		this.cupomRepository = cupomRepository;
	}

	public CompraBuilderPasso3 executa(InfoPadraoCheckoutRequest infoPadrao,
			String codigoProduto, String codigoOferta) throws BindException {

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

		// aqui podia ter ficado um passo só sim
		CompraBuilderPasso2 basicoDaCompra = CompraBuilder.nova(conta, oferta);

		if (infoPadrao.temCodigoCupom()) {
			Cupom cupom = infoPadrao.buscaCodigoCupom().flatMap(codigo -> {
				return cupomRepository.findByCodigoAndProdutoId(codigo,
						produto.getId());
			}).orElseThrow(() -> BindExceptionFactory.createGlobalError(
					new Object(), "error",
					"Não existe um cupom com este código para este produto"));

			basicoDaCompra.setCupom(cupom);
		}

		return basicoDaCompra.passoPagamento();
	}

}
