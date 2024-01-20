package com.deveficiente.desafiocheckouthotmart.checkout;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto.ConfiguracaoBoleto;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto.NovoCheckoutBoletoRequest;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao.NovoCheckoutCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.cupom.Cupom;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

public class CompraBuilder {

	public static class CompraBuilderPasso3 {

		private Conta conta;
		private Oferta oferta;
		private Optional<Cupom> possivelCupom;

		CompraBuilderPasso3(Conta conta, Oferta oferta, Cupom cupom) {
			this.conta = conta;
			this.oferta = oferta;
			//aqui eu aceito nulo porque são classes usadas apenas internamente
			this.possivelCupom = Optional.ofNullable(cupom);
			// TODO Auto-generated constructor stub
		}

		public String getCombinacaoContaOferta() {
			return this.conta.getCodigo().toString()
					.concat(this.oferta.getCodigo().toString());
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
				// aqui tem que usar a compra direto, não precisa mais da oferta
				// e com a compra na mao da para saber o valor final e qual vai ser o valor da parcela também
				metadados.setInfoCompraCartao(request.toInfoCompraCartao(compra));

				return metadados;
			};

			return possivelCupom
					.map(cupom -> new Compra(conta, oferta, cupom,funcaoCriadoraMetadados))
					.orElse(new Compra(conta, oferta,funcaoCriadoraMetadados));
			
		}

		public Compra comBoleto(NovoCheckoutBoletoRequest request,
				ConfiguracaoBoleto configuracaoBoleto) {
			Function<Compra, MetadadosCompra> funcaoCriadoraMetadados = compra -> {
				MetadadosCompra metadados = new MetadadosCompra(compra);
				metadados.setInfoBoleto(new InfoCompraBoleto(request.getCpf(),
						configuracaoBoleto.geraCodigoParaBoleto(),
						compra.getPreco(),
						configuracaoBoleto.dataExpiracao(LocalDate.now())));

				return metadados;
			};

			return possivelCupom
					.map(cupom -> new Compra(conta, oferta, cupom,funcaoCriadoraMetadados))
					.orElse(new Compra(conta, oferta,funcaoCriadoraMetadados));
		}		
	}
	
	
	public static class CompraBuilderPasso2 {

		private Conta conta;
		private Oferta oferta;
		private Cupom cupom;

		public CompraBuilderPasso2(Conta conta, Oferta oferta) {
			this.conta = conta;
			this.oferta = oferta;
		}



		public void setCupom(Cupom cupom) {
			this.cupom = cupom;
			
		}

		public CompraBuilderPasso3 passoPagamento() {
			return new CompraBuilderPasso3(conta,oferta,cupom);
		}



		public Long getProdutoId() {
			return this.oferta.getProduto().getId();
		}

	}

	public static CompraBuilderPasso2 nova(Conta conta, Oferta oferta) {
		return new CompraBuilderPasso2(conta, oferta);
	}

}
