package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGatewayClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailRequest;
import com.deveficiente.desafiocheckouthotmart.compartilhado.DynamicTemplateRunner;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Erro500Exception;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.compartilhado.PartialClass;
import com.deveficiente.desafiocheckouthotmart.compartilhado.RemoteHttpClient;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

/**
 * Aqui está concentrado todo fluxo de criacao de uma {@link Compra} utilizando
 * o cartão de credito como meio de pagamento.
 * 
 * @author albertoluizsouza
 *
 */
@ICP(10)
@PartialClass(PagaComCartaoCreditoController.class)
@Component
public class FluxoRealizacaoCompraCartao {

	private ExecutaTransacao executaTransacao;
	@ICP
	private CompraRepository compraRepository;
	private RemoteHttpClient remoteHttpClient;
	@ICP
	private CartaoGatewayClient cartaoGatewayClient;
	private DynamicTemplateRunner dynamicTemplateRunner;
	@ICP
	private Provider1EmailClient provider1EmailClient;
	private Retry retryCartao;

	public FluxoRealizacaoCompraCartao(ExecutaTransacao executaTransacao,
			@ICP CompraRepository compraRepository,
			RemoteHttpClient remoteHttpClient,
			@ICP CartaoGatewayClient cartaoGatewayClient,
			DynamicTemplateRunner dynamicTemplateRunner,
			@ICP Provider1EmailClient provider1EmailClient,
			@Qualifier("retryCartao") Retry retry) {
		super();
		this.executaTransacao = executaTransacao;
		this.compraRepository = compraRepository;
		this.remoteHttpClient = remoteHttpClient;
		this.cartaoGatewayClient = cartaoGatewayClient;
		this.dynamicTemplateRunner = dynamicTemplateRunner;
		this.provider1EmailClient = provider1EmailClient;
		this.retryCartao = retry;
	}

	private static final Logger log = LoggerFactory
			.getLogger(FluxoRealizacaoCompraCartao.class);

	/**
	 * 
	 * @param oferta
	 * @param conta
	 * @param request
	 */
	public Compra executa(@ICP Oferta oferta, @ICP Conta conta,
			NovoCheckoutCartaoRequest request) {
		@ICP
		NovoPagamentoGatewayCartaoRequest requestGateway = request
				.toPagamentoGatewayCartaoRequest(oferta);

		@ICP
		Compra novaCompra = executaTransacao.comRetorno(() -> {
			/*
			 * O builder aqui é pq eu já sei que vai ter maneiras diferentes de
			 * criar uma nova compra em função da forma de pagamento. Então já
			 * tentei criar um mecanismo pode ser evoluido. O basico é sempre
			 * relacionar com uma conta e uma oferta e depois complementar com o
			 * tipo de pagamento específico.
			 */

			return compraRepository.save(CompraBuilder.nova(conta, oferta)
					.comCartao(requestGateway));
		});

		
		
		Result<RuntimeException, String> resultadoIntegracaoCartao = remoteHttpClient
				.execute(() -> {
					return Decorators.ofSupplier(() -> {
						Log5WBuilder.metodo().oQueEstaAcontecendo("Vai processar o pagamento")
						.adicionaInformacao("request", requestGateway.toString())
						.info(log);
						
						return cartaoGatewayClient.executa(requestGateway);						
					})
					.withRetry(retryCartao)
					.get();
				});

		// @ICP ifSucess
		// @ICP e ifProblem
		return resultadoIntegracaoCartao.ifSuccess(idTransacao -> {

			Log5WBuilder.metodo().oQueEstaAcontecendo("Processou o pagamento")
					.adicionaInformacao("request", idTransacao)
					.adicionaInformacao("codigoConta",
							conta.getCodigo().toString())
					.info(log);

			// deveria logar que vai atualizar a compra. Já que isso aqui vai
			// parar no banco de dados.
			// so que cansa mesmo hehe. Como melhorar?

			executaTransacao.comRetorno(() -> {
				novaCompra.finaliza(idTransacao);
				return novaCompra;
			});

			String body = dynamicTemplateRunner.buildTemplate(
					"template-email-nova-compra.html",
					Map.of("compra", novaCompra));

			@ICP
			Provider1EmailRequest emailRequest = new Provider1EmailRequest(
					"Compra aprovada", "checkout@hotmart.com", conta.getEmail(),
					body);

			Log5WBuilder.metodo().oQueEstaAcontecendo("Vai enviar o email")
					.adicionaInformacao("codigo da compra",
							novaCompra.getCodigo().toString())
					.adicionaInformacao("email", emailRequest.toString())
					.info(log);

			provider1EmailClient.sendEmail(emailRequest);

			Log5WBuilder.metodo().oQueEstaAcontecendo("Enviou o email")
					.adicionaInformacao("codigo da compra",
							novaCompra.getCodigo().toString())
					.info(log);

			return novaCompra;
		}).ifProblem(Erro500Exception.class, (erro) -> {
			Log5WBuilder.metodo()
					.oQueEstaAcontecendo("Vai enviar o email de problema")
					.adicionaInformacao("codigo da compra",
							novaCompra.getCodigo().toString())
					.adicionaInformacao("email", "email de problema").info(log);

//			provider1EmailClient.sendEmail(emailRequest);

			Log5WBuilder.metodo()
					.oQueEstaAcontecendo("Enviou o email de problema")
					.adicionaInformacao("codigo da compra",
							novaCompra.getCodigo().toString())
					.info(log);

			// retorna a compra mesmo assim, afinal de contas ela foi criada.
			return novaCompra;
		}).ifProblem(Exception.class, e -> {
			Log5WBuilder.metodo().oQueEstaAcontecendo(
					"Aconteceu um problema inesperado na integracao com o cartao de credito")
					.adicionaInformacao("codigoCompra",
							novaCompra.getCodigo().toString())
					.debug(log);
			return novaCompra;
		}).execute().get();
	}

}
