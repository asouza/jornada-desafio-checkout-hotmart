package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraRepository;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.ContaRepository;



@RestController
public class CallbackLiberacaoBoletoController {

	private ContaRepository contaRepository;
	private CompraRepository compraRepository;

	public CallbackLiberacaoBoletoController(ContaRepository contaRepository,
			CompraRepository compraRepository) {
		super();
		this.contaRepository = contaRepository;
		this.compraRepository = compraRepository;
	}

	@PostMapping("/conta/{codigoConta}/pagamentos/boletos/pendentes")
	public void executa(@PathVariable("codigoConta") String codigoConta,
			@UUID String codigoBoleto) {
		
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
		
		System.out.println("vai processar...");

	}
}
