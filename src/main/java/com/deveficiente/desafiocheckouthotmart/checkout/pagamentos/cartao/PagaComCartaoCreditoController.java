package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao;

import java.math.BigDecimal;

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
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.BuscasNecessariasParaPagamento;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.CriaOBasicoDaCompraParaFluxosWeb;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;

import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@RestController
@ICP(10)
public class PagaComCartaoCreditoController {

	@ICP
	private FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao;
	@ICP
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	@ICP
	private CriaOBasicoDaCompraParaFluxosWeb basicoDaCompraParaFluxosWeb;

	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(PagaComCartaoCreditoController.class);

	public PagaComCartaoCreditoController(
			@ICP FluxoRealizacaoCompraCartao fluxoRealizacaoCompraCartao,
			@ICP BuscasNecessariasParaPagamento buscasNecessariasParaPagamento,
			@ICP CriaOBasicoDaCompraParaFluxosWeb basicoDaCompraParaFluxosWeb,
			EntityManager manager) {
		super();
		this.fluxoRealizacaoCompraCartao = fluxoRealizacaoCompraCartao;
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
		this.basicoDaCompraParaFluxosWeb = basicoDaCompraParaFluxosWeb;
		this.manager = manager;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * JSR-303 validated property 'dadosCartao.anoVencimento' does not have
		 * a corresponding accessor for Spring data binding - check your
		 * DataBinder's configuration (bean property versus direct field access)
		 * 
		 * Eu tinha tomado essa exception quando tinha falhado a validacao de
		 * data no futuro. Realmente a exception dava uma dica, mas confesso que
		 * não associei mexer no DataBinder na hora. Estava sem a configuração
		 * dele naquele momento.
		 * 
		 * Joguei o problema no chatgpt e ele sugeriu usar a solução abaixo,
		 * funcionou mesmo.
		 */
		binder.initDirectFieldAccess();
	}

	@PostMapping("/checkouts/produtos/{codigoProduto}/{codigoOferta}")
	public Retorno2 executa(@PathVariable("codigoProduto") String codigoProduto,
			@PathVariable("codigoOferta") String codigoOferta,
			@Valid @RequestBody @ICP NovoCheckoutCartaoRequest request) throws BindException {

		CompraBuilderPasso3 basicoDaCompra = basicoDaCompraParaFluxosWeb
				.executa(request.getInfoPadrao(), codigoProduto, codigoOferta);

		@ICP
		Result<RuntimeException, Long> resultado = fluxoRealizacaoCompraCartao
				.executa(basicoDaCompra, request);
		
		if(resultado.isSuccess()) {
			Compra compraCriada = manager.find(Compra.class, resultado.getSuccessReturn());
			return new Retorno2(compraCriada.getCodigo().toString(),
					compraCriada.getOferta().getPreco());			
		}
		
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Não poi possível concluir a compra");


	}

	public static record Retorno(String codigo) {

	}

	public static record Retorno2(String codigo, BigDecimal preco) {

	}
}
