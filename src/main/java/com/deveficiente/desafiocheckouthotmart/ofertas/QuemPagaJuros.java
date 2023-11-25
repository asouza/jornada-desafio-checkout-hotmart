package com.deveficiente.desafiocheckouthotmart.ofertas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public enum QuemPagaJuros {

	cliente {
		@Override
		List<ValorParcelaMes> calculaParcelas(
				@NotNull @Positive BigDecimal preco, BigDecimal taxaJurosAoMes,
				@Min(1) int numeroMaximoParcelas) {
			Assert.isTrue(numeroMaximoParcelas >= 1,
					"O número de parcelas precisar ser maior ou igual a 1");
									
			ArrayList<ValorParcelaMes> parcelas = new ArrayList<>();
			parcelas.add(new ValorParcelaMes(preco, 1));
			
			System.out.println(numeroMaximoParcelas+"====");
			
			for(int n = 2; n <= numeroMaximoParcelas; n++ ) {
				BigDecimal taxaPercentualTotal = taxaJurosAoMes
							.multiply(new BigDecimal(n))
							.divide(new BigDecimal("100"))
							.add(BigDecimal.ONE);
				
				System.out.println("Taxa total ="+taxaPercentualTotal);
				
				System.out.println(preco + "/" + new BigDecimal(n));				
				BigDecimal valorPorMesSemJuros = preco.divide(new BigDecimal(n),3,RoundingMode.HALF_EVEN);
				BigDecimal valorPorMesComJuros = valorPorMesSemJuros.multiply(taxaPercentualTotal);
				parcelas.add(new ValorParcelaMes(valorPorMesComJuros, n));
			}
			return parcelas;
		}
	},vendedor {
		@Override
		List<ValorParcelaMes> calculaParcelas(
				@NotNull @Positive BigDecimal preco, BigDecimal taxaJuros,
				@Min(1) int numeroMaximoParcelas) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * 
	 * @param preco
	 * @param taxaJurosAoMes juros a ser cobrado por mês
	 * @param numeroMaximoParcelas
	 * @return
	 */
	abstract List<ValorParcelaMes> calculaParcelas(@NotNull @Positive BigDecimal preco,
			BigDecimal taxaJurosAoMes, @Min(1) int numeroMaximoParcelas);
}
