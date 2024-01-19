package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.function.BiFunction;

import com.deveficiente.desafiocheckouthotmart.checkout.CompraBuilder.CompraBuilderPasso3;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;

public class EtapaPagamento {

	public static class EtapaPagamentoFluxoEspecifico<RequestType> {

		private CompraBuilderPasso3 passoPagamento;
		private RequestType request;

		public EtapaPagamentoFluxoEspecifico(CompraBuilderPasso3 passoPagamento,
				RequestType request) {
			this.passoPagamento = passoPagamento;
			this.request = request;
		}

		public Result<RuntimeException, CompraId> executa(
				BiFunction<CompraBuilderPasso3, RequestType, Result<RuntimeException, CompraId>> fluxoEspecifico) {
			//aqui ainda poderia rodar alguma checagem de pós condição.
			return fluxoEspecifico.apply(passoPagamento, request);
		}

	}

	private CompraBuilderPasso3 passoPagamento;

	public EtapaPagamento(CompraBuilderPasso3 passoPagamento) {
		this.passoPagamento = passoPagamento;
	}

	public <RequestType> EtapaPagamentoFluxoEspecifico<RequestType> comRequest(
			RequestType request) {
		return new EtapaPagamentoFluxoEspecifico<RequestType>(passoPagamento,
				request);
	}

}
