package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

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
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGatewayClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailClient;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.ConfiguracaoRepository;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.ContaRepository;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.deveficiente.desafiocheckouthotmart.produtos.ProdutoRepository;

import jakarta.validation.Valid;

@RestController
@ICP(12)
public class PagaComCartaoCreditoController {

	@ICP
	private ProdutoRepository produtoRepository;
	@ICP
	private ContaRepository contaRepository;
	@ICP
	private ConfiguracaoRepository configuracaoRepository;
	@ICP
	private CartaoGatewayClient cartaoGatewayClient;
	private ExecutaTransacao executaTransacao;
	@ICP
	private CompraRepository compraRepository;
	@ICP
	private Provider1EmailClient provider1EmailClient;
	@ICP
	private FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComCartaoCreditoController.class);

	public PagaComCartaoCreditoController(ProdutoRepository produtoRepository,
			ContaRepository contaRepository,
			ConfiguracaoRepository configuracaoRepository,
			CartaoGatewayClient cartaoGatewayClient,
			ExecutaTransacao executaTransacao,
			CompraRepository compraRepository,
			Provider1EmailClient provider1EmailClient,
			FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao) {
		super();
		this.produtoRepository = produtoRepository;
		this.contaRepository = contaRepository;
		this.configuracaoRepository = configuracaoRepository;
		this.cartaoGatewayClient = cartaoGatewayClient;
		this.executaTransacao = executaTransacao;
		this.compraRepository = compraRepository;
		this.provider1EmailClient = provider1EmailClient;
		this.fluxoRealizacaoCompraCartao = fluxoRealizacaoCompraCartao;
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
	public void executa(@PathVariable("codigoProduto") String codigoProduto,
			@PathVariable("codigoOferta") String codigoOferta,
			@Valid @RequestBody @ICP NovoCheckoutCartaoRequest request) {

		/*
		 * TODO Será que essa sequencia de produto + busca de oferta pode virar
		 * um Domain Service
		 */

		@ICP
		Produto produto = OptionalToHttpStatusException.execute(
				produtoRepository.findByCodigo(UUID.fromString(codigoProduto)),
				404, "Produto não encontrado");

		@ICP
		Optional<Conta> possivelConta = contaRepository
				.findByEmail(request.getInfoPadrao().getEmail());

		Conta conta = executaTransacao.comRetorno(() -> {
			return possivelConta.orElseGet(() -> {
				@ICP
				Configuracao configuracaoDefault = configuracaoRepository
						.getByOpcaoDefaultIsTrue();
				Assert.notNull(configuracaoDefault,
						"Deveria haver uma configuracao default criada");

				Conta contaGravada = contaRepository.save(
						request.getInfoPadrao().novaConta(configuracaoDefault));

				Log5WBuilder
						// se pega o método automático aqui captura o lambda
						.metodo("PagaComCartaoCreditoController#executa")
						.oQueEstaAcontecendo(
								"Novo pagamento: salvando uma nova conta")
						.adicionaInformacao("codigoNovaConta",
								contaGravada.getCodigo().toString())
						.info(log);

				return contaGravada;
			});

		});

		@ICP
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		Compra compraCriada = fluxoRealizacaoCompraCartao.executa(oferta, conta,
				request);

	}
}
