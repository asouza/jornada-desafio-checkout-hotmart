package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class PagaComCartaoCreditoController {

	@PostMapping("/checkouts/produtos/{codigoProduto}/{codigoOferta}")
	public void executa(@PathVariable("codigoProduto") String codigoProduto,
			@PathVariable("codigoOferta") String codigoOferta,
			@Valid @RequestBody NovoCheckoutCartaoRequest request) {
		System.out.println(request);

	}
}
