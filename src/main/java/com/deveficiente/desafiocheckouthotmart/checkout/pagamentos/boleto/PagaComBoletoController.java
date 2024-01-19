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
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.CompraId;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.TemplateFluxoPagamento;
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
	private TemplateFluxoPagamento templateFluxoPagamento;
	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComBoletoController.class);

	public PagaComBoletoController(
			@ICP FluxoRealizacaoCompraBoleto fluxoRealizacaoCompraBoleto,
			@ICP TemplateFluxoPagamento templateFluxoPagamento,
			EntityManager manager) {
		super();
		this.fluxoRealizacaoCompraBoleto = fluxoRealizacaoCompraBoleto;
		this.templateFluxoPagamento = templateFluxoPagamento;
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

		Result<RuntimeException, CompraId> resultado = templateFluxoPagamento
				.executa(request, codigoProduto, codigoOferta,fluxoRealizacaoCompraBoleto
						::executa);

		if (resultado.isSuccess()) {
			Compra compra = manager.find(Compra.class,
					resultado.getSuccessReturn().idCompra());

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
