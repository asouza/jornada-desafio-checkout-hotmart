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

import com.deveficiente.desafiocheckouthotmart.clientesremotos.CartaoGatewayClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.NovoPagamentoGatewayCartaoRequest;
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
public class PagaComCartaoCreditoController {

	private ProdutoRepository produtoRepository;
	private ContaRepository contaRepository;
	private ConfiguracaoRepository configuracaoRepository;
	private CartaoGatewayClient cartaoGatewayClient;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComCartaoCreditoController.class);

	public PagaComCartaoCreditoController(ProdutoRepository produtoRepository,
			ContaRepository contaRepository,
			ConfiguracaoRepository configuracaoRepository,
			CartaoGatewayClient cartaoGatewayClient) {
		super();
		this.produtoRepository = produtoRepository;
		this.contaRepository = contaRepository;
		this.configuracaoRepository = configuracaoRepository;
		this.cartaoGatewayClient = cartaoGatewayClient;
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
			@Valid @RequestBody NovoCheckoutCartaoRequest request) {

		/*
		 * TODO Será que essa sequencia de produto + busca de oferta pode virar
		 * um Domain Service
		 */

		Produto produto = OptionalToHttpStatusException.execute(
				produtoRepository.findByCodigo(UUID.fromString(codigoProduto)),
				404, "Produto não encontrado");

		Optional<Conta> possivelConta = contaRepository
				.findByEmail(request.getInfoPadrao().getEmail());

		Conta conta = possivelConta.orElseGet(() -> {
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
		
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		NovoPagamentoGatewayCartaoRequest requestGateway 
			= request.toPagamentoGatewayCartaoRequest(oferta);
		
		Log5WBuilder
			.metodo()
			.oQueEstaAcontecendo("Vai processar o pagamento")
			.adicionaInformacao("request", requestGateway.toString())
			.info(log);
		
		String idTransacao = cartaoGatewayClient.executa(requestGateway);
		
		Log5WBuilder
			.metodo()
			.oQueEstaAcontecendo("Processou o pagamento")
			.adicionaInformacao("request", idTransacao)
			.adicionaInformacao("codigoConta", conta.getCodigo().toString())
			.info(log);		

		/*
		 * - precisa verificar se já existe o usuário com o email 
		 * - se não existe conta, salva. 
		 * - precisa processar o pagamento. 
		 * - Tem que mandar o numero de parcelas e o valor por parcela 
		 * - cria uma compra associando o id de transacao 
		 * - politica de retry? - grava a compra
		 * - associada com o id da transacao - manda email para as situacoes
		 */

	}
}
