package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso3;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.CriaOBasicoDaCompraParaFluxosWeb;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@RestController
@ICP(10)
public class PagaComBoletoController {

	@ICP
	private FluxoRealizacaoCompraBoleto fluxoRealizacaoCompraBoleto;
	@ICP
	private CriaOBasicoDaCompraParaFluxosWeb criaOBasicoDaCompraParaFluxosWeb;
	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComBoletoController.class);

	public PagaComBoletoController(
			@ICP FluxoRealizacaoCompraBoleto fluxoRealizacaoCompraBoleto,
			@ICP CriaOBasicoDaCompraParaFluxosWeb criaOBasicoDaCompraParaFluxosWeb,
			EntityManager manager) {
		super();
		this.fluxoRealizacaoCompraBoleto = fluxoRealizacaoCompraBoleto;
		this.criaOBasicoDaCompraParaFluxosWeb = criaOBasicoDaCompraParaFluxosWeb;
		this.manager = manager;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.initDirectFieldAccess();
	}

	@PostMapping("/checkouts/produtos/{codigoProduto}/{codigoOferta}/boleto")
	public Map<String, String> executa(
			@PathVariable("codigoProduto") String codigoProduto,
			@PathVariable("codigoOferta") String codigoOferta,
			@Valid @RequestBody @ICP NovoCheckoutBoletoRequest request) throws BindException {

		CompraBuilderPasso3 basicoDaCompra = criaOBasicoDaCompraParaFluxosWeb
				.executa(request.getInfoPadrao(), codigoProduto, codigoOferta);

		/*
		 * Com esse lance do controle de fluxo, tem um monte de transacao
		 * rolando... O melhor parece ser retornar ids e o proximo fluxo
		 * reconstroi o objeto.
		 */
		Result<RuntimeException, Long> resultado = fluxoRealizacaoCompraBoleto
				.executa(basicoDaCompra, request);

		if (resultado.isSuccess()) {
			Compra compra = manager.find(Compra.class,
					resultado.getSuccessReturn());

			return Map.of("codigoCompra", compra.getCodigo().toString(),
					"ultimoStatus",
					compra.getUltimaTransacaoRegistrada().getStatus()
							.toString(),
					"codigoBoleto", compra.getMetadados()
							.buscaInfoCompraBoleto().get().getCodigoBoleto());

		}

		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
				"Não foi possível concluir a geração do boleto");

	}
}
