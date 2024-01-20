package com.deveficiente.desafiocheckouthotmart.ofertas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.checkout.FormulaCalculoJuros;
import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public enum QuemPagaJuros {

	cliente {
		@Override
		public List<ValorParcelaMes> calculaParcelasParaCliente(
				@NotNull @Positive BigDecimal preco, BigDecimal taxaJurosAoMes,
				@Min(1) int numeroMaximoParcelas) {
			Assert.isTrue(numeroMaximoParcelas >= 1,
					"O número de parcelas precisar ser maior ou igual a 1");
									
			ArrayList<ValorParcelaMes> parcelas = new ArrayList<>();
			parcelas.add(new ValorParcelaMes(preco, 1));
			
			for(int n = 2; n <= numeroMaximoParcelas; n++ ) {
				BigDecimal valorParcela = FormulaCalculoJuros.executa(preco, taxaJurosAoMes, n,5, RoundingMode.HALF_EVEN);
				parcelas.add(new ValorParcelaMes(valorParcela, n));
			}
			return parcelas;
		}

		@Override
		public BigDecimal calculaPossivelDescontoDeRepasse(BigDecimal valor,
				@Positive Integer numeroParcelas, BigDecimal taxaJuros) {

			return BigDecimal.ZERO;
		}

		@Override
		public
		ValorParcelaMes calculaParcelaEspecificaParaCliente(
				@Positive @NotNull BigDecimal preco, BigDecimal taxaJuros,
				@Min(1) int numeroParcelas) {
			BigDecimal valorParcela = FormulaCalculoJuros.executa(preco, taxaJuros, numeroParcelas,5, RoundingMode.HALF_EVEN);
			return new ValorParcelaMes(valorParcela, numeroParcelas);
		}
	},vendedor {
		@Override
		public List<ValorParcelaMes> calculaParcelasParaCliente(
				@NotNull @Positive BigDecimal preco, BigDecimal taxaJuros,
				@Min(1) int numeroMaximoParcelas) {
			Assert.isTrue(numeroMaximoParcelas >= 1,
					"O número de parcelas precisar ser maior ou igual a 1");
									
			ArrayList<ValorParcelaMes> parcelas = new ArrayList<>();
			parcelas.add(new ValorParcelaMes(preco, 1));
			
			for(int n = 2; n <= numeroMaximoParcelas; n++ ) {
				BigDecimal valorPorMesSemJuros = preco.divide(new BigDecimal(n),3,RoundingMode.HALF_EVEN);
				parcelas.add(new ValorParcelaMes(valorPorMesSemJuros, n));
			}
			return parcelas;		}

		@Override
		public BigDecimal calculaPossivelDescontoDeRepasse(BigDecimal valor,
				@Positive Integer numeroParcelas, BigDecimal taxaJuros) {
			
			BigDecimal valorParcela = FormulaCalculoJuros.executa(valor, taxaJuros, numeroParcelas,5, RoundingMode.HALF_EVEN);
			BigDecimal valorTotalASerPagoComJuros = valorParcela.multiply(new BigDecimal(numeroParcelas));
			
			BigDecimal descontoRepasse = valorTotalASerPagoComJuros.subtract(valor);
			
			return descontoRepasse;
		}

		@Override
		public
		ValorParcelaMes calculaParcelaEspecificaParaCliente(
				@Positive @NotNull BigDecimal preco, BigDecimal taxaJuros,
				@Min(1) int numeroParcelas) {
			BigDecimal valorPorMesSemJuros = preco.divide(new BigDecimal(numeroParcelas),3,RoundingMode.HALF_EVEN);
			return new ValorParcelaMes(valorPorMesSemJuros, numeroParcelas);
		}
	};
	
	/**
	 * 
	 * @param preco
	 * @param taxaJurosAoMes juros a ser cobrado por mês
	 * @param numeroMaximoParcelas
	 * @return
	 */
	public abstract List<ValorParcelaMes> calculaParcelasParaCliente(@NotNull @Positive BigDecimal preco,
			BigDecimal taxaJurosAoMes, @Min(1) int numeroMaximoParcelas);

	public abstract BigDecimal calculaPossivelDescontoDeRepasse(BigDecimal valor,
			@Positive Integer numeroParcelas, BigDecimal taxaJuros);

	public abstract ValorParcelaMes calculaParcelaEspecificaParaCliente(
			@Positive @NotNull BigDecimal preco, BigDecimal taxaJuros,
			@Min(1) int numeroParcelas);
}
