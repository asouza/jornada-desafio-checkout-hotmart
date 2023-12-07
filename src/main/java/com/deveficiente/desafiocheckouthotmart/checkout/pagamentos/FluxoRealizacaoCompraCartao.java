package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.checkout.EmailsCompra;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGateway1Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;
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
@ICP(9)
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
	private JmsTemplate jmsTemplate;

	

	public FluxoRealizacaoCompraCartao(ExecutaTransacao executaTransacao,
			@ICP CompraRepository compraRepository,
			RemoteHttpClient remoteHttpClient,
			@ICP CartaoGateway1Client cartaoGatewayClient, Retry retryCartao,
			@ICP ProximoGatewayPagamento proximoGatewayPagamento,
			@ICP EmailsCompra emailsCompra, JmsTemplate jmsTemplate) {
		super();
		this.executaTransacao = executaTransacao;
		this.compraRepository = compraRepository;
		this.remoteHttpClient = remoteHttpClient;
		this.cartaoGatewayClient = cartaoGatewayClient;
		this.retryCartao = retryCartao;
		this.proximoGatewayPagamento = proximoGatewayPagamento;
		this.emailsCompra = emailsCompra;
		this.jmsTemplate = jmsTemplate;
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

			return compraRepository
					.save(CompraBuilder.nova(conta, oferta).comCartao(request));
		});

		Result<RuntimeException, String> resultadoIntegracaoCartao = remoteHttpClient
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
					}).withRetry(retryCartao).get();
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

			
			Decorators.ofSupplier(() -> {
				emailsCompra.enviaSucesso(conta, novaCompra);
				return null;
			})
			.withFallback(exception -> {
				Map<String, String> parametrosEmail = 
						Map.of("codigoConta",
								conta.getCodigo().toString(),
								"codigoCompra",
								novaCompra.getCodigo().toString());
				
				Log5WBuilder
					.metodo()
					.oQueEstaAcontecendo("Colocando o email de sucesso para ser disparado via fila")
					.adicionaInformacao("request", idTransacao)
					.adicionaInformacao("codigoConta",
						conta.getCodigo().toString())
					.info(log);				
				
				/*
				 * O fallback aqui quebrou o constant work pattern. O fluxo normal
				 * é síncrono e o fluxo alternativo assíncrono. O que vai ser mostrado
				 * para o cliente? Email foi enviado? Email ainda vai ser enviado?
				 */
				jmsTemplate.convertAndSend("envia-email-sucesso-compra", parametrosEmail);
				
				Log5WBuilder
					.metodo()
					.oQueEstaAcontecendo("Enviou o email de sucesso para ser disparado via fila")
					.adicionaInformacao("request", idTransacao)
					.adicionaInformacao("codigoConta",
							conta.getCodigo().toString())
					.info(log);				
					return null;
			})
			.get();
			

			return novaCompra;
		}).ifProblem(Erro500Exception.class, (erro) -> {
			
			emailsCompra.enviaEmailFalha(conta,novaCompra);
			
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
