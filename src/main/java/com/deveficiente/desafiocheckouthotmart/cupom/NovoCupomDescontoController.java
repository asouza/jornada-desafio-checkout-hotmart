package com.deveficiente.desafiocheckouthotmart.cupom;

import java.util.UUID;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.produtos.JaExisteCupomComMesmoCodigoException;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.deveficiente.desafiocheckouthotmart.produtos.ProdutoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@ICP(6)
public class NovoCupomDescontoController {

	@ICP
	private ProdutoRepository produtoRepository;

	public NovoCupomDescontoController(ProdutoRepository produtoRepository) {
		super();
		this.produtoRepository = produtoRepository;
	}

	@PostMapping("/produtos/{codigoProduto}/cupons")
	@Transactional
	public void executa(@PathVariable("codigoProduto") UUID codigoProduto,
			@Valid @RequestBody @ICP NovoCupomDescontoRequest request) throws BindException {

		@ICP
		Produto produto = OptionalToHttpStatusException.execute(
				produtoRepository.findByCodigo(codigoProduto), 404,
				"Produto inexistente");

		/*
		 * E agora, isso ou Either?
		 */
		//@ICP
		try {
			produto.adicionaCupom(request::toModel);
		}
		//@ICP
		catch (@ICP JaExisteCupomComMesmoCodigoException e) {
			throw BindExceptionFactory.createGlobalError(request, "error",
					"Já existe uma oferta com o mesmo código. "+e.getCodigoCupom());
		}

	}
}
