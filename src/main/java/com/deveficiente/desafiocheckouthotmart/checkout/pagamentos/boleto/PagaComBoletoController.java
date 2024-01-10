package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.BuscasNecessariasParaPagamento;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.CriaOBasicoDaCompraParaFluxosWeb;
import com.deveficiente.desafiocheckouthotmart.checkout.RegistraNovaContaService;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@RestController
@ICP(10)
public class PagaComBoletoController {

	@ICP
	private FluxoRealizacaoCompraBoleto fluxoRealizacaoCompraBoleto;
	@ICP
	private CriaOBasicoDaCompraParaFluxosWeb criaOBasicoDaCompraParaFluxosWeb;
	private ExecutaTransacao executaTransacao;
	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComBoletoController.class);

	public PagaComBoletoController(
			@ICP FluxoRealizacaoCompraBoleto fluxoRealizacaoCompraBoleto,
			@ICP CriaOBasicoDaCompraParaFluxosWeb criaOBasicoDaCompraParaFluxosWeb,
			ExecutaTransacao executaTransacao, EntityManager manager) {
		super();
		this.fluxoRealizacaoCompraBoleto = fluxoRealizacaoCompraBoleto;
		this.criaOBasicoDaCompraParaFluxosWeb = criaOBasicoDaCompraParaFluxosWeb;
		this.executaTransacao = executaTransacao;
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
			@Valid @RequestBody @ICP NovoCheckoutBoletoRequest request) {

		CompraBuilderPasso2 basicoDaCompra = criaOBasicoDaCompraParaFluxosWeb
				.executa(request.getInfoPadrao(), codigoProduto, codigoOferta);

		/*
		 * Com esse lance do controle de fluxo, tem um monte de transacao
		 * rolando... O melhor parece ser retornar ids e o proximo fluxo
		 * reconstroi o objeto.
		 */
		@ICP
		Long idCompra = fluxoRealizacaoCompraBoleto
				.executa(basicoDaCompra, request);

		Compra compra = manager.find(Compra.class, idCompra);

		return Map.of("codigoCompra", compra.getCodigo().toString(),
				"ultimoStatus",
				compra.getUltimaTransacaoRegistrada().getStatus()
						.toString(),
				"codigoBoleto", compra.getMetadados()
						.buscaInfoCompraBoleto().get().getCodigoBoleto());

	}
}
