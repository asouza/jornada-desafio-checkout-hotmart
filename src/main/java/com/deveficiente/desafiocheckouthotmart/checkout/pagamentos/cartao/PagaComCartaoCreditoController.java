package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.BuscasNecessariasParaPagamento;
import com.deveficiente.desafiocheckouthotmart.checkout.RegistraNovaContaService;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.validation.Valid;

@RestController
@ICP(10)
public class PagaComCartaoCreditoController {

	private ExecutaTransacao executaTransacao;
	@ICP
	private FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao;
	@ICP
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	private RegistraNovaContaService registraNovaContaService;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComCartaoCreditoController.class);

	public PagaComCartaoCreditoController(ExecutaTransacao executaTransacao,
			FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao,
			BuscasNecessariasParaPagamento buscasNecessariasParaPagamento,
			RegistraNovaContaService registraNovaContaService) {
		super();
		this.executaTransacao = executaTransacao;
		this.fluxoRealizacaoCompraCartao = fluxoRealizacaoCompraCartao;
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
		this.registraNovaContaService = registraNovaContaService;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * JSR-303 validated property 'dadosCartao.anoVencimento' does not have
		 * a corresponding accessor for Spring data binding - check your
		 * DataBinder's configuration (bean property versus direct field access)
		 * 
		 * Eu tinha tomado essa exception quando tinha falhado a validacao de
		 * data no futuro. Realmente a exception dava uma dica, mas confesso que
		 * não associei mexer no DataBinder na hora. Estava sem a configuração
		 * dele naquele momento.
		 * 
		 * Joguei o problema no chatgpt e ele sugeriu usar a solução abaixo,
		 * funcionou mesmo.
		 */
		binder.initDirectFieldAccess();
	}

	@PostMapping("/checkouts/produtos/{codigoProduto}/{codigoOferta}")
	public Retorno2 executa(@PathVariable("codigoProduto") String codigoProduto,
			@PathVariable("codigoOferta") String codigoOferta,
			@Valid @RequestBody @ICP NovoCheckoutCartaoRequest request) {


		Conta conta = executaTransacao.comRetorno(() -> {
			return registraNovaContaService.executa(
					request.getInfoPadrao().getEmail(),
					request.getInfoPadrao()::novaConta);
			
		});
		
		
		
		@ICP
		Produto produto = OptionalToHttpStatusException
				.execute(buscasNecessariasParaPagamento.buscaProdutoPorCodigo(
						codigoProduto), 404, "Produto não encontrado");

		@ICP
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		CompraBuilderPasso2 basicoDaCompra = CompraBuilder.nova(conta, oferta);		
		
		@ICP
		Compra compraCriada = fluxoRealizacaoCompraCartao.executa(basicoDaCompra,
				request);

		return new Retorno2(compraCriada.getCodigo().toString(),
				compraCriada.getOferta().getPreco());

	}

	public static record Retorno(String codigo) {

	}

	public static record Retorno2(String codigo, BigDecimal preco) {

	}
}
