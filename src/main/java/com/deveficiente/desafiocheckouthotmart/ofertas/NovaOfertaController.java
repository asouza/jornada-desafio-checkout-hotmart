package com.deveficiente.desafiocheckouthotmart.ofertas;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.produtos.JaExisteOfertaComMesmoNomeException;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.deveficiente.desafiocheckouthotmart.produtos.ProdutoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
//TODO tests
public class NovaOfertaController {

	private ProdutoRepository produtoRepository;

	public NovaOfertaController(ProdutoRepository produtoRepository) {
		super();
		this.produtoRepository = produtoRepository;
	}

	@PostMapping("/produtos/{codigoProduto}/ofertas")
	@Transactional
	public Map<String, String> executa(@PathVariable String codigoProduto,
			@Valid @RequestBody NovaOfertaRequest request) throws Exception {

		// TODO da para criar uma wrapper em cima do repository que já lança
		// esse treco
		Produto produto = produtoRepository
				.findByCodigo(UUID.fromString(codigoProduto)).orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								"Produto não encontrado"));

		Result<RuntimeException, Oferta> resultado = produto
				.adicionaOferta(request::toModel);

		return resultado.ifSuccess(oferta -> {
			produto.tentaDefinirOfertaComoPrincipal(oferta);
			return Map.of("nome", oferta.getNome(), "principal",
					oferta.isPrincipal() + "");
		}).throwsEarlyIf(JaExisteOfertaComMesmoNomeException.class, erro -> {
			return BindExceptionFactory.createGlobalError(request,
					"novaOfertaRequest",
					"Já existe uma oferta com mesmo nome para este produto. "
							+ erro.getOferta().getNome());
		}).execute().get();
	}
}
