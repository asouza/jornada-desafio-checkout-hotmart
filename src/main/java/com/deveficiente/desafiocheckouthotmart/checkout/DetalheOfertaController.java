package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.deveficiente.desafiocheckouthotmart.produtos.ProdutoRepository;

import jakarta.validation.constraints.NotBlank;

@RestController
public class DetalheOfertaController {

	private ProdutoRepository produtoRepository;

	public DetalheOfertaController(ProdutoRepository produtoRepository) {
		super();
		this.produtoRepository = produtoRepository;
	}

	@GetMapping("/checkouts/produtos/{codigoProduto}")
	public CheckoutDetalheOfertaResponse executa(@PathVariable("codigoProduto") String codigoProduto,
			@NotBlank @RequestParam String codigoOferta) {
		Produto produto = OptionalToHttpStatusException.execute(
				produtoRepository.findByCodigo(UUID.fromString(codigoProduto)),
				404, "Produto não encontrado");

		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());
		
		return new CheckoutDetalheOfertaResponse(oferta); 		

	}
}
