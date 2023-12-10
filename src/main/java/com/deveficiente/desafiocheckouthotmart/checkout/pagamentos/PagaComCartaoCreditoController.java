package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGateway1Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailClient;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@RestController
@ICP(10)
public class PagaComCartaoCreditoController {

	@ICP
	private CartaoGateway1Client cartaoGatewayClient;
	private ExecutaTransacao executaTransacao;
	@ICP
	private Provider1EmailClient provider1EmailClient;
	@ICP
	private FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao;
	@ICP
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComCartaoCreditoController.class);

	public PagaComCartaoCreditoController(
			CartaoGateway1Client cartaoGatewayClient,
			ExecutaTransacao executaTransacao,
			Provider1EmailClient provider1EmailClient,
			FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao,
			BuscasNecessariasParaPagamento buscasNecessariasParaPagamento,
			EntityManager manager) {
		super();
		this.cartaoGatewayClient = cartaoGatewayClient;
		this.executaTransacao = executaTransacao;
		this.provider1EmailClient = provider1EmailClient;
		this.fluxoRealizacaoCompraCartao = fluxoRealizacaoCompraCartao;
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
		this.manager = manager;
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

		/*
		 * TODO Será que essa sequencia de produto + busca de oferta pode virar
		 * um Domain Service
		 */

		@ICP
		Optional<Conta> possivelConta = buscasNecessariasParaPagamento
				.findContaByEmail(request.getInfoPadrao().getEmail());

		Conta conta = executaTransacao.comRetorno(() -> {
			return possivelConta.orElseGet(() -> {
				@ICP
				Configuracao configuracaoDefault = buscasNecessariasParaPagamento
						.getConfiguracaoDefault();

				Assert.notNull(configuracaoDefault,
						"Deveria haver uma configuracao default criada");

				Conta novaConta = request.getInfoPadrao().novaConta(configuracaoDefault);
				manager.persist(novaConta);

				Log5WBuilder
						// se pega o método automático aqui captura o lambda
						.metodo("PagaComCartaoCreditoController#executa")
						.oQueEstaAcontecendo(
								"Novo pagamento: salvando uma nova conta")
						.adicionaInformacao("codigoNovaConta",
								novaConta.getCodigo().toString())
						.info(log);

				return novaConta;
			});

		});

		@ICP
		Produto produto = OptionalToHttpStatusException
				.execute(buscasNecessariasParaPagamento.buscaProdutoPorCodigo(
						codigoProduto), 404, "Produto não encontrado");

		@ICP
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		@ICP
		Compra compraCriada = fluxoRealizacaoCompraCartao.executa(oferta, conta,
				request);
		
		return new Retorno2(compraCriada.getCodigo().toString(),compraCriada.getOferta().getPreco());

	}
	
	public static record Retorno(String codigo) {
		
	}
	
	public static record Retorno2(String codigo,BigDecimal preco) {

	}
}
