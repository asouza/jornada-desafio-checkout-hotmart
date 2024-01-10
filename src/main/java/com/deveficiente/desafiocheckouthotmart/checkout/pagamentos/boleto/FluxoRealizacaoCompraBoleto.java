package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.checkout.EmailsCompra;
import com.deveficiente.desafiocheckouthotmart.checkout.FluxoEnviaEmailSucesso;
import com.deveficiente.desafiocheckouthotmart.checkout.StatusCompra;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples.BoletoApiClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples.NovoBoletoRequest;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Erro500Exception;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.compartilhado.PartialClass;
import com.deveficiente.desafiocheckouthotmart.compartilhado.RemoteHttpClient;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.compartilhado.steps.BusinessFlowRegister;
import com.deveficiente.desafiocheckouthotmart.compartilhado.steps.BusinessFlowSteps;

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
	@ICP
	private ConfiguracaoBoleto configuracaoBoleto;

	@Autowired
	private BusinessFlowRegister businessFlowRegister;

	public FluxoRealizacaoCompraBoleto(ExecutaTransacao executaTransacao,
			@ICP CompraRepository compraRepository,
			RemoteHttpClient remoteHttpClient, Retry retryDefault,
			@ICP EmailsCompra emailsCompra,
			CircuitBreaker circuitBreakerDefault,
			@ICP FluxoEnviaEmailSucesso fluxoEnviaEmailSucesso,
			@ICP BoletoApiClient boletoApiClient,
			ConfiguracaoBoleto configuracaoBoleto) {
		super();
		this.executaTransacao = executaTransacao;
		this.compraRepository = compraRepository;
		this.remoteHttpClient = remoteHttpClient;
		this.retryDefault = retryDefault;
		this.emailsCompra = emailsCompra;
		this.circuitBreakerDefault = circuitBreakerDefault;
		this.fluxoEnviaEmailSucesso = fluxoEnviaEmailSucesso;
		this.boletoApiClient = boletoApiClient;
		this.configuracaoBoleto = configuracaoBoleto;
	}

	private static final Logger log = LoggerFactory
			.getLogger(FluxoRealizacaoCompraBoleto.class);

	/**
	 * 
	 * @param oferta
	 * @param conta
	 * @param request
	 */
	public Long executa(CompraBuilderPasso2 basicoDaCompra,
			NovoCheckoutBoletoRequest request) {

		BusinessFlowSteps businessFlowSteps = businessFlowRegister
				.execute("compraComBoleto", basicoDaCompra
						.getCombinacaoContaOferta().concat(request.getCpf()));

		String idCompra = businessFlowSteps.executeOnlyOnce("criaCompra",
				() -> {
					System.out.println("Gravando nova compra com boleto...");
					@ICP
					Compra novaCompra = executaTransacao.comRetorno(() -> {
						/*
						 * O builder aqui é pq eu já sei que vai ter maneiras
						 * diferentes de criar uma nova compra em função da
						 * forma de pagamento. Então já tentei criar um
						 * mecanismo pode ser evoluido. O basico é sempre
						 * relacionar com uma conta e uma oferta e depois
						 * complementar com o tipo de pagamento específico.
						 */

						return compraRepository.save(basicoDaCompra
								.comBoleto(request, configuracaoBoleto));
					});

					return novaCompra.getId();
				});

		Compra compraGravada = compraRepository.findById(Long.valueOf(idCompra))
				.get();

		Result<RuntimeException, String> resultadoIntegracao = remoteHttpClient
				.execute(() -> {
					/*
					 * O flow aqui o id da transação
					 */
					return businessFlowSteps.executeOnlyOnce("integraApiBoleto",
							() -> {
								System.out.println(
										"Realizando integracao com a api de boleto...");
								return Decorators.ofSupplier(() -> {
									Log5WBuilder.metodo()
											.oQueEstaAcontecendo(
													"Vai processar o pagamento")
											.adicionaInformacao("compra",
													compraGravada.toString())
											.info(log);

									return boletoApiClient
											.executa(new NovoBoletoRequest(
													compraGravada));
								}).withCircuitBreaker(circuitBreakerDefault)
										.withRetry(retryDefault).get();
							});

				});

		// @ICP ifSucess
		// @ICP e ifProblem
		return resultadoIntegracao.ifSuccess(idTransacao -> {

			businessFlowSteps.executeOnlyOnce("adicionaTransacao", () -> {
				Log5WBuilder.metodo()
						.oQueEstaAcontecendo("Processou o pagamento")
						.adicionaInformacao("codigoCompra",
								compraGravada.getCodigo().toString())
						.adicionaInformacao("idTransacao", idTransacao)
						.adicionaInformacao("codigoConta",
								compraGravada.getCodigoConta().toString())
						.info(log);

				// deveria logar que vai atualizar a compra. Já que isso aqui
				// vai
				// parar no banco de dados.
				// so que cansa mesmo hehe. Como melhorar?

				Log5WBuilder.metodo().oQueEstaAcontecendo(
						"Vai alterar o status da compra para gerando boleto")
						.adicionaInformacao("codigoCompra",
								compraGravada.getCodigo().toString())
						.info(log);

				Long idCompraAlterada = executaTransacao.comRetorno(() -> {
					compraGravada
							.adicionaTransacao(StatusCompra.gerando_boleto);
					return compraGravada.getId();
				});

				Log5WBuilder.metodo().oQueEstaAcontecendo(
						"Alterou o status da compra para gerando boleto")
						.adicionaInformacao("codigoCompra",
								compraGravada.getCodigo().toString())
						.info(log);

				return idCompraAlterada;
			});

			return compraGravada.getId();
		}).ifProblem(Erro500Exception.class, (erro) -> {

			businessFlowSteps.executeOnlyOnce("enviaEmailDeFalha", () -> {
				System.out.println("Enviando email de falha");
				emailsCompra.enviaEmailFalha(compraGravada);
				return "emailFalhaEnviado";

			});

			// retorna a compra mesmo assim, afinal de contas ela foi criada.
			return compraGravada.getId();
		}).ifProblem(Exception.class, e -> {
			Log5WBuilder.metodo().oQueEstaAcontecendo(
					"Aconteceu um problema inesperado na integracao com a api de boleto")
					.adicionaInformacao("codigoCompra",
							compraGravada.getCodigo().toString())
					.info(log);
			return compraGravada.getId();
		}).execute().get();
	}

}
