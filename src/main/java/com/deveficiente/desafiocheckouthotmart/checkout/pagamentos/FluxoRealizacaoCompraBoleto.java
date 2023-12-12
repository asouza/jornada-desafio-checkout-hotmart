package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.checkout.EmailsCompra;
import com.deveficiente.desafiocheckouthotmart.checkout.FluxoEnviaEmailSucesso;
import com.deveficiente.desafiocheckouthotmart.checkout.StatusCompra;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples.BoletoApiClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples.NovoBoletoRequest;
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
@PartialClass(PagaComBoletoController.class)
@Component
public class FluxoRealizacaoCompraBoleto {

	private ExecutaTransacao executaTransacao;
	@ICP
	private CompraRepository compraRepository;
	private RemoteHttpClient remoteHttpClient;
	private Retry retryDefault;
	@ICP
	private EmailsCompra emailsCompra;
	private CircuitBreaker circuitBreakerDefault;
	@ICP
	private FluxoEnviaEmailSucesso fluxoEnviaEmailSucesso;
	@ICP
	private BoletoApiClient boletoApiClient;

	public FluxoRealizacaoCompraBoleto(ExecutaTransacao executaTransacao,
			@ICP CompraRepository compraRepository,
			RemoteHttpClient remoteHttpClient, Retry retryDefault,
			@ICP EmailsCompra emailsCompra, CircuitBreaker circuitBreakerDefault,
			@ICP FluxoEnviaEmailSucesso fluxoEnviaEmailSucesso,
			@ICP BoletoApiClient boletoApiClient) {
		super();
		this.executaTransacao = executaTransacao;
		this.compraRepository = compraRepository;
		this.remoteHttpClient = remoteHttpClient;
		this.retryDefault = retryDefault;
		this.emailsCompra = emailsCompra;
		this.circuitBreakerDefault = circuitBreakerDefault;
		this.fluxoEnviaEmailSucesso = fluxoEnviaEmailSucesso;
		this.boletoApiClient = boletoApiClient;
	}

	private static final Logger log = LoggerFactory
			.getLogger(FluxoRealizacaoCompraBoleto.class);

	/**
	 * 
	 * @param oferta
	 * @param conta
	 * @param request
	 */
	public Compra executa(CompraBuilderPasso2 basicoDaCompra,
			NovoCheckoutBoletoRequest request) {

		String codigoBoleto = UUID.randomUUID().toString();
		LocalDate dataExpiracao = LocalDate.now().plusDays(3);
		

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
					.save(basicoDaCompra.comBoleto(request, codigoBoleto,dataExpiracao));
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
					return Decorators.ofSupplier(() -> {
						Log5WBuilder.metodo()
								.oQueEstaAcontecendo(
										"Vai processar o pagamento")
								.adicionaInformacao("compra",
										novaCompra.toString())
								.info(log);

						return boletoApiClient.executa(new NovoBoletoRequest(novaCompra));
					})
					.withCircuitBreaker(circuitBreakerDefault)
					.withRetry(retryDefault).get();
				});

		// @ICP ifSucess
		// @ICP e ifProblem
		return resultadoIntegracao.ifSuccess(idTransacao -> {

			Log5WBuilder.metodo().oQueEstaAcontecendo("Processou o pagamento")
					.adicionaInformacao("codigoCompra", novaCompra.getCodigo().toString())
					.adicionaInformacao("idTransacao", idTransacao)
					.adicionaInformacao("codigoConta",
							novaCompra.getCodigoConta().toString())
					.info(log);

			// deveria logar que vai atualizar a compra. Já que isso aqui vai
			// parar no banco de dados.
			// so que cansa mesmo hehe. Como melhorar?

			executaTransacao.comRetorno(() -> {
				novaCompra.adicionaTransacao(StatusCompra.gerando_boleto);
				return novaCompra;
			});

			return novaCompra;
		}).ifProblem(Erro500Exception.class, (erro) -> {

			emailsCompra.enviaEmailFalha(novaCompra);

			// retorna a compra mesmo assim, afinal de contas ela foi criada.
			return novaCompra;
		}).ifProblem(Exception.class, e -> {
			Log5WBuilder.metodo().oQueEstaAcontecendo(
					"Aconteceu um problema inesperado na integracao com a api de boleto")
					.adicionaInformacao("codigoCompra",
							novaCompra.getCodigo().toString())
					.debug(log);
			return novaCompra;
		}).execute().get();
	}

}
