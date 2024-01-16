package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso2;
import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.PartialClass;
import com.deveficiente.desafiocheckouthotmart.cupom.Cupom;
import com.deveficiente.desafiocheckouthotmart.cupom.CupomRepository;

@PartialClass(CriaOBasicoDaCompraParaFluxosWeb.class)
@Component
@ICP(8)
public class FluxoAplicaoCupom {

	@ICP
	private CupomRepository cupomRepository;

	public FluxoAplicaoCupom(CupomRepository cupomRepository) {
		super();
		this.cupomRepository = cupomRepository;
	}

	/**
	 * 
	 * @param infoPadrao
	 * @param passo2
	 * @throws BindException
	 * @changeState Ele pode alterar o estado do parametro de tipo {@link CompraBuilderPasso2}
	 */
	public void executa(@ICP InfoPadraoCheckoutRequest infoPadrao,@ICP CompraBuilderPasso2 passo2) throws BindException {
		/*
		 * Aqui é uma implementação que faz parte do fluxo principal
		 * marcado no partial class. A ideia é codar como se estivesse
		 * lá. 
		 */
		//@ICP
		if (infoPadrao.temCodigoCupom()) {
			@ICP(3)
			Cupom cupom = infoPadrao.buscaCodigoCupom().flatMap(codigo -> {
				return cupomRepository.findByCodigoAndProdutoId(codigo,
						passo2.getProdutoId());
			}).orElseThrow(() -> BindExceptionFactory.createGlobalError(
					new Object(), "error",
					"Não existe um cupom com este código para este produto"));
			
			//@ICP
			if(!cupom.isValido()) {
				throw BindExceptionFactory.createGlobalError("O cupom não está mais válido");
			}

			passo2.setCupom(cupom);
		}		
	}

}
