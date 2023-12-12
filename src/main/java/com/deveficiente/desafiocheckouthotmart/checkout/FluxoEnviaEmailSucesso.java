package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import io.github.resilience4j.decorators.Decorators;

@Component
public class FluxoEnviaEmailSucesso {

	@ICP
	private EmailsCompra emailsCompra;
	private JmsTemplate jmsTemplate;

	private static final Logger log = LoggerFactory
			.getLogger(FluxoEnviaEmailSucesso.class);

	public FluxoEnviaEmailSucesso(@ICP EmailsCompra emailsCompra,
			JmsTemplate jmsTemplate) {
		super();
		this.emailsCompra = emailsCompra;
		this.jmsTemplate = jmsTemplate;
	}

	public void executa(@ICP Compra novaCompra) {
		Optional<String> possivelIdTransacao = novaCompra.buscaIdTransacao();
		Assert.isTrue(possivelIdTransacao.isPresent(),
				"Só pode enviar email de sucesso para compra que já foi finalizada. Id = "
						+ novaCompra.getCodigo());

		Decorators.ofSupplier(() -> {
			emailsCompra.enviaSucesso(novaCompra);
			return null;
		}).withFallback(exception -> {
			Map<String, String> parametrosEmail = Map.of("codigoConta",
					novaCompra.getCodigoConta().toString(), "codigoCompra",
					novaCompra.getCodigo().toString());

			Log5WBuilder.metodo().oQueEstaAcontecendo(
					"Colocando o email de sucesso para ser disparado via fila")
					.adicionaInformacao("codigoCompra",
							novaCompra.getCodigo().toString())
					.adicionaInformacao("codigoConta",
							novaCompra.getCodigoConta().toString())
					.info(log);

			/*
			 * O fallback aqui quebrou o constant work pattern. O fluxo normal é
			 * síncrono e o fluxo alternativo assíncrono. O que vai ser mostrado
			 * para o cliente? Email foi enviado? Email ainda vai ser enviado?
			 */
			jmsTemplate.convertAndSend("envia-email-sucesso-compra",
					parametrosEmail);

			Log5WBuilder.metodo().oQueEstaAcontecendo(
					"Enviou o email de sucesso para ser disparado via fila")
					.adicionaInformacao("codigoCompra",
							novaCompra.getCodigo().toString())
					.adicionaInformacao("codigoConta",
							novaCompra.getCodigoConta().toString())
					.info(log);
			return null;
		}).get();
	}

}
