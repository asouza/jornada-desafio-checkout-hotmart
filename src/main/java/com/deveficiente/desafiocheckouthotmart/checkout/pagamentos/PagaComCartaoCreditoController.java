package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class PagaComCartaoCreditoController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * JSR-303 validated property 'dadosCartao.anoVencimento' 
		 * does not have a corresponding accessor for Spring data binding 
		 * - check your DataBinder's configuration 
		 * (bean property versus direct field access)
		 * 
		 * Eu tinha tomado essa exception quando tinha falhado a validacao
		 * de data no futuro. Realmente a exception dava uma dica, mas 
		 * confesso que não associei mexer no DataBinder na hora. Estava 
		 * sem a configuração dele naquele momento. 
		 * 
		 * Joguei o problema no chatgpt e ele sugeriu usar a solução abaixo, 
		 * funcionou mesmo. 
		 */
	    binder.initDirectFieldAccess();
	}	

	@PostMapping("/checkouts/produtos/{codigoProduto}/{codigoOferta}")
	public void executa(@PathVariable("codigoProduto") String codigoProduto,
			@PathVariable("codigoOferta") String codigoOferta,
			@Valid @RequestBody NovoCheckoutCartaoRequest request) {
		System.out.println(request);

	}
}
