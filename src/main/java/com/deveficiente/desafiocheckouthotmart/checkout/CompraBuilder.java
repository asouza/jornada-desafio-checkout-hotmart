package com.deveficiente.desafiocheckouthotmart.checkout;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.NovoCheckoutBoletoRequest;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.NovoCheckoutCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

public class CompraBuilder {

	public static class CompraBuilderPasso2 {

		private Conta conta;
		private Oferta oferta;

		public CompraBuilderPasso2(Conta conta, Oferta oferta) {
			this.conta = conta;
			this.oferta = oferta;
		}

		public Compra comCartao(NovoCheckoutCartaoRequest request) {

			/*
			 * Eu uso uma funcao aqui pq eu quero criar um metadado linkado com
			 * a compra.. Mas eu não tenho uma compra ainda. E tento sempre
			 * preservar a prática de não alterar referencias criadas por outros
			 * lugares.
			 */
			Function<Compra, MetadadosCompra> funcaoCriadoraMetadados = compra -> {
				MetadadosCompra metadados = new MetadadosCompra(compra);
				// TODO refatorar aqui. Isso aqui foi exagero
				metadados.setInfoCompraCartao(
						oferta -> request.toInfoCompraCartao(oferta));

				return metadados;
			};

			return new Compra(conta, oferta, funcaoCriadoraMetadados);
		}

		public Compra comBoleto(NovoCheckoutBoletoRequest request,
				String codigoBoleto, LocalDate dataExpiracao) {
			Function<Compra, MetadadosCompra> funcaoCriadoraMetadados = compra -> {
				MetadadosCompra metadados = new MetadadosCompra(compra);
				metadados.setInfoBoleto(new InfoCompraBoleto(request.getCpf(),
						codigoBoleto, compra.getPreco(), dataExpiracao));

				return metadados;
			};

			return new Compra(conta, oferta, funcaoCriadoraMetadados);
		}

	}

	public static CompraBuilderPasso2 nova(Conta conta, Oferta oferta) {
		return new CompraBuilderPasso2(conta, oferta);
	}

}
