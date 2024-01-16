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
@ICP(16)
public class CriaOBasicoDaCompraParaFluxosWeb {
	
	private ExecutaTransacao executaTransacao;
	@ICP
	private RegistraNovaContaService registraNovaContaService;
	@ICP
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	@ICP
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

	
	public @ICP CompraBuilderPasso3 executa(@ICP InfoPadraoCheckoutRequest infoPadrao,
			String codigoProduto, String codigoOferta) throws BindException {

		@ICP(2)
		Conta conta = executaTransacao.comRetorno(() -> {
			return registraNovaContaService.executa(infoPadrao.getEmail());

		});

		@ICP
		Produto produto = OptionalToHttpStatusException
				.execute(buscasNecessariasParaPagamento.buscaProdutoPorCodigo(
						codigoProduto), 404, "Produto não encontrado");

		@ICP(2)
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		// aqui podia ter ficado um passo só sim
		@ICP
		CompraBuilderPasso2 basicoDaCompra = CompraBuilder.nova(conta, oferta);

		//@ICP
		if (infoPadrao.temCodigoCupom()) {
			@ICP(3)
			Cupom cupom = infoPadrao.buscaCodigoCupom().flatMap(codigo -> {
				return cupomRepository.findByCodigoAndProdutoId(codigo,
						produto.getId());
			}).orElseThrow(() -> BindExceptionFactory.createGlobalError(
					new Object(), "error",
					"Não existe um cupom com este código para este produto"));
			
			//@ICP
			if(!cupom.isValido()) {
				throw BindExceptionFactory.createGlobalError("O cupom não está mais válido");
			}

			basicoDaCompra.setCupom(cupom);
		}

		return basicoDaCompra.passoPagamento();
	}

}
