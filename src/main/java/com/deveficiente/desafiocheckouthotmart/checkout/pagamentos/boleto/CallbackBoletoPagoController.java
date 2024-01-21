package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.checkout.EmailsCompra;
import com.deveficiente.desafiocheckouthotmart.checkout.FluxoEnviaEmailSucesso;
import com.deveficiente.desafiocheckouthotmart.checkout.StatusCompra;
import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.compartilhado.steps.BusinessFlowEntity;
import com.deveficiente.desafiocheckouthotmart.compartilhado.steps.BusinessFlowRegister;
import com.deveficiente.desafiocheckouthotmart.compartilhado.steps.BusinessFlowSteps;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.ContaRepository;

import jakarta.persistence.EntityManager;

@RestController
public class CallbackBoletoPagoController {

	private ContaRepository contaRepository;
	private CompraRepository compraRepository;
	private ExecutaTransacao executaTransacao;
	private EmailsCompra emailsCompra;
	private BusinessFlowRegister businessFlowRegister;
	@Autowired
	private EntityManager manager;

	public CallbackBoletoPagoController(ContaRepository contaRepository,
			CompraRepository compraRepository,
			ExecutaTransacao executaTransacao, EmailsCompra emailsCompra,
			BusinessFlowRegister businessFlowRegister) {
		super();
		this.contaRepository = contaRepository;
		this.compraRepository = compraRepository;
		this.executaTransacao = executaTransacao;
		this.emailsCompra = emailsCompra;
		this.businessFlowRegister = businessFlowRegister;
	}

	@PostMapping("/conta/{codigoConta}/pagamentos/boletos/pago")
	public void executa(@PathVariable("codigoConta") String codigoConta,
			@UUID String codigoBoleto,
			StatusBoletoSimples statusBoletoSimples) throws BindException {

		Compra compra = OptionalToHttpStatusException.execute(
				compraRepository.buscaPorCodigoBoleto(codigoBoleto), 404,
				"Não existe compra para este boleto");

		Conta conta = OptionalToHttpStatusException.execute(
				contaRepository
						.findByCodigo(java.util.UUID.fromString(codigoConta)),
				404, "Não existe a conta com o código " + codigoConta);

		if (!compra.pertenceConta(conta)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		BusinessFlowSteps fluxoBoletoPago = businessFlowRegister.execute(
				"Fluxo boleto pago", compra.getCodigo().toString().concat("boleto_pago"));

		if (statusBoletoSimples.equals(StatusBoletoSimples.paid)) {
			fluxoBoletoPago.executeOnlyOnce("adicionaTransacao", () -> {
				return executaTransacao.comRetorno(() -> {
					Compra compraNaNovaTransacao = manager.merge(compra);
					compraNaNovaTransacao.finaliza(codigoBoleto);
					return compraNaNovaTransacao.getId();
				});
			});

			fluxoBoletoPago.executeOnlyOnce("enviaEmail", () -> {
				//TODO aqui o melhor é usar o novo objeto de compra
				emailsCompra.enviaSucesso(compra);
				return "email-boleto-pago";
			});
			
			return ;

		}
		
		throw BindExceptionFactory.createGlobalError("Aqui só deveria chegar o status do "+StatusBoletoSimples.paid+". Só que chegou "+statusBoletoSimples);

	}
}
