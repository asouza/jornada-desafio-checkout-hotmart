package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailClient;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailRequest;
import com.deveficiente.desafiocheckouthotmart.compartilhado.DynamicTemplateRunner;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

@Component
public class EmailsCompra {

	@ICP
	private DynamicTemplateRunner dynamicTemplateRunner;
	@ICP
	private Provider1EmailClient provider1EmailClient;

	private static final Logger log = LoggerFactory
			.getLogger(EmailsCompra.class);

	public EmailsCompra(DynamicTemplateRunner dynamicTemplateRunner,
			Provider1EmailClient provider1EmailClient) {
		super();
		this.dynamicTemplateRunner = dynamicTemplateRunner;
		this.provider1EmailClient = provider1EmailClient;
	}

	public void enviaSucesso(@ICP Compra novaCompra) {
		String body = dynamicTemplateRunner.buildTemplate(
				"template-email-nova-compra.html",
				Map.of("compra", novaCompra));

		@ICP
		Provider1EmailRequest emailRequest = new Provider1EmailRequest(
				"Compra aprovada", "checkout@hotmart.com", novaCompra.getConta().getEmail(),
				body);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Vai enviar o email")
				.adicionaInformacao("codigo da compra",
						novaCompra.getCodigo().toString())
				.adicionaInformacao("email", emailRequest.toString()).info(log);

		provider1EmailClient.sendEmail(emailRequest);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Enviou o email")
				.adicionaInformacao("codigo da compra",
						novaCompra.getCodigo().toString())
				.info(log);
	}

	public void enviaEmailFalha(Compra novaCompra) {
		Log5WBuilder.metodo()
				.oQueEstaAcontecendo("Vai enviar o email de problema")
				.adicionaInformacao("codigo da compra",
						novaCompra.getCodigo().toString())
				.adicionaInformacao("email", "email de problema").info(log);

		//provider1EmailClient.sendEmail(emailRequest);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Enviou o email de problema")
				.adicionaInformacao("codigo da compra",
						novaCompra.getCodigo().toString())
				.info(log);

	}

	public void mandaBoleto(Compra compra) {
		String body = dynamicTemplateRunner.buildTemplate(
				"template-email-boleto-gerado.html",
				Map.of("compra", compra));

		@ICP
		Provider1EmailRequest emailRequest = new Provider1EmailRequest(
				"Compra aprovada", "checkout@hotmart.com", compra.getConta().getEmail(),
				body);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Vai enviar o email")
				.adicionaInformacao("codigo da compra",
						compra.getCodigo().toString())
				.adicionaInformacao("email", emailRequest.toString()).info(log);

		provider1EmailClient.sendEmail(emailRequest);

		Log5WBuilder.metodo().oQueEstaAcontecendo("Enviou o email")
				.adicionaInformacao("codigo da compra",
						compra.getCodigo().toString())
				.info(log);		
	}

}
