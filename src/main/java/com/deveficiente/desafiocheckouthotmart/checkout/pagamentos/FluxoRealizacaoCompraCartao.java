package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.checkout.EmailsCompra;
import com.deveficiente.desafiocheckouthotmart.checkout.FluxoEnviaEmailSucesso;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGateway1Client;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Erro500Exception;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.compartilhado.PartialClass;
import com.deveficiente.desafiocheckouthotmart.compartilhado.RemoteHttpClient;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;

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
	private CartaoGateway1Client cartaoGatewayClient;
	private Retry retryCartao;
	@ICP
	private ProximoGatewayPagamento proximoGatewayPagamento;
	@ICP
	private EmailsCompra emailsCompra;
	private CircuitBreaker circuitBreakerCartao;
	@ICP
	private FluxoEnviaEmailSucesso fluxoEnviaEmailSucesso;

	public FluxoRealizacaoCompraCartao(ExecutaTransacao executaTransacao,
			CompraRepository compraRepository,
			RemoteHttpClient remoteHttpClient,
			CartaoGateway1Client cartaoGatewayClient, Retry retryCartao,
			ProximoGatewayPagamento proximoGatewayPagamento,
			EmailsCompra emailsCompra, CircuitBreaker circuitBreakerCartao,
			FluxoEnviaEmailSucesso fluxoEnviaEmailSucesso) {
		super();
		this.executaTransacao = executaTransacao;
		this.compraRepository = compraRepository;
		this.remoteHttpClient = remoteHttpClient;
		this.cartaoGatewayClient = cartaoGatewayClient;
		this.retryCartao = retryCartao;
		this.proximoGatewayPagamento = proximoGatewayPagamento;
		this.emailsCompra = emailsCompra;
		this.circuitBreakerCartao = circuitBreakerCartao;
		this.fluxoEnviaEmailSucesso = fluxoEnviaEmailSucesso;
	}

	private static final Logger log = LoggerFactory
			.getLogger(FluxoRealizacaoCompraCartao.class);

	/**
	 * 
	 * @param oferta
	 * @param conta
	 * @param request
	 */
	public Compra executa(CompraBuilderPasso2 basicoDaCompra,
			NovoCheckoutCartaoRequest request) {
		/*
		 * Essa ideia aqui morre com múltiplos gateways. Vai ser necessário
		 * inverter a decisão... Passa a request para construir o dto específico
		 * da integração.
		 * 
		 * É preciso identificar quem é o "maior" e quem é o "menor". O menor
		 * aqui é a request web, então ela não pode conhecer todas
		 * implementações de dto de integração com o cartão.
		 * 
		 * O código tende a seguir a dependencia do maior para o menor.
		 */
//		@ICP
//		NovoPagamentoGatewayCartao1Request requestGateway = request
//				.toPagamentoGatewayCartaoRequest(oferta);

		@ICP
		Compra novaCompra = executaTransacao.comRetorno(() -> {
			/*
			 * O builder aqui é pq eu já sei que vai ter maneiras diferentes de
			 * criar uma nova compra em função da forma de pagamento. Então já
			 * tentei criar um mecanismo pode ser evoluido. O basico é sempre
			 * relacionar com uma conta e uma oferta e depois complementar com o
			 * tipo de pagamento específico.
			 */

			return compraRepository.save(basicoDaCompra.comCartao(request));
		});

		Result<RuntimeException, String> resultadoIntegracao = remoteHttpClient
				.execute(() -> {

					/*
					 * Aqui antes eu tava recebendo uma request e uma oferta. Só
					 * que eu tenho um padrão que preciso documentar, se eu já
					 * computei uma variavel em função de outras, eu não posso
					 * mais usar aquelas variaveis no mesmo fluxo. Se eu uso,
					 * pode ser um sinal que alguma coisa ficou mal desenhada.
					 */
					Supplier<String> proximoGateway = proximoGatewayPagamento
							.proximoGateway(novaCompra);

					return Decorators.ofSupplier(() -> {
						Log5WBuilder.metodo()
								.oQueEstaAcontecendo(
										"Vai processar o pagamento")
								.adicionaInformacao("compra",
										novaCompra.toString())
								.info(log);

						return proximoGateway.get();
					})
							/*
							 * A ordem aqui importa. O primeiro decorator é
							 * aplicado primeiro.. Então aqui, se a chamada
							 * falha, o circuitBreaker é incrementado e depois
							 * rola o retry. Isso quer dizer que se tiver
							 * chegado no limte ele nem vai tentar a próxima.
							 * 
							 * Só que não rola fazer a política aqui do token
							 * bucket estilo amazon, para isso acontecer cada
							 * falha deveria ser contabilizar no circuitbreaker.
							 */
							.withCircuitBreaker(circuitBreakerCartao)
							.withRetry(retryCartao).get();
				});

		// @ICP ifSucess
		// @ICP e ifProblem
		return resultadoIntegracao.ifSuccess(idTransacao -> {

			Log5WBuilder.metodo().oQueEstaAcontecendo("Processou o pagamento")
					.adicionaInformacao("request", idTransacao)
					.adicionaInformacao("codigoConta",
							novaCompra.getCodigoConta().toString())
					.info(log);

			// deveria logar que vai atualizar a compra. Já que isso aqui vai
			// parar no banco de dados.
			// so que cansa mesmo hehe. Como melhorar?

			executaTransacao.comRetorno(() -> {
				novaCompra.finaliza(idTransacao);
				return novaCompra;
			});

			fluxoEnviaEmailSucesso.executa(novaCompra);

			return novaCompra;
		}).ifProblem(Erro500Exception.class, (erro) -> {

			emailsCompra.enviaEmailFalha(novaCompra);

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
