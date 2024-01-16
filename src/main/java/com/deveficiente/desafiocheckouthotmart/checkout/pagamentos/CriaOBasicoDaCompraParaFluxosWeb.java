package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso3;
import com.deveficiente.desafiocheckouthotmart.checkout.RegistraNovaContaService;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.OptionalToHttpStatusException;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

@Component
@ICP(12)
/*
 * Alberto: O caminho que encontrei para refatorar para abaixo de
 * 10 pontos ia basicamente levar a complexidade daqui para o outro 
 * lugar. Não achei que estava dividindo a complexidade de fato. 
 */
public class CriaOBasicoDaCompraParaFluxosWeb {
	
	private ExecutaTransacao executaTransacao;
	@ICP
	private RegistraNovaContaService registraNovaContaService;
	@ICP
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	@ICP
	private FluxoAplicaoCupom fluxoAplicacaoCupom;

	public CriaOBasicoDaCompraParaFluxosWeb(ExecutaTransacao executaTransacao,
			RegistraNovaContaService registraNovaContaService,
			BuscasNecessariasParaPagamento buscasNecessariasParaPagamento,	
			FluxoAplicaoCupom fluxoAplicacaoCupom) {
		super();
		this.executaTransacao = executaTransacao;
		this.registraNovaContaService = registraNovaContaService;
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
		this.fluxoAplicacaoCupom = fluxoAplicacaoCupom;
	}

	
	public @ICP CompraBuilderPasso3 executa(@ICP InfoPadraoCheckoutRequest infoPadrao,
			String codigoProduto, String codigoOferta) throws BindException {

		@ICP(2)
		Conta conta = executaTransacao.comRetorno(() -> {
			return registraNovaContaService.executa(infoPadrao.getEmail());

		});
				
		@ICP
		Produto produto = OptionalToHttpStatusException
				.execute(buscasNecessariasParaPagamento.buscaProdutoPorCodigo(
						codigoProduto), 404, "Produto não encontrado");

		@ICP(2)
		Oferta oferta = produto.buscaOferta(UUID.fromString(codigoOferta))
				.orElseGet(() -> produto.getOfertaPrincipal());

		// aqui podia ter ficado um passo só sim
		@ICP
		CompraBuilderPasso2 basicoDaCompra = CompraBuilder.nova(conta, oferta);
		
		fluxoAplicacaoCupom.executa(infoPadrao,basicoDaCompra);

		return basicoDaCompra.passoPagamento();
	}

}
