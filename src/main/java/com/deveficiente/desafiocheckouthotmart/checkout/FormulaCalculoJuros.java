package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe para isolar o calculo de juros
 * @author albertoluizsouza
 *
 */
public class FormulaCalculoJuros {

	/**
	 * 
	 * @param valorOriginal
	 * @param taxaAoMes ex: 3.5
	 * @param numeroParcelas
	 * @param casasDecimais
	 * @param arrendondamento
	 * @return
	 */
	public static BigDecimal executa(BigDecimal valorOriginal,BigDecimal taxaAoMes,int numeroParcelas,int casasDecimais,RoundingMode arrendondamento) {
		//     parte 1             parte 2                                  parte 3 
		//PMT= valorOriginal × i(1+taxaAoMesPercentual)ˆnumeroParcelas / (1 + taxaAoMesPercentual)ˆnumeroParcelas - 1
		
		//eu pedi para o chatgpt me explicar
		
		//explicacao da hotmart => https://help.hotmart.com/pt-br/article/quais-os-tipos-de-parcelamentos-disponiveis-na-hotmart/360040081131
		
		
		
		BigDecimal taxaAoMesPercentual = taxaAoMes.divide(new BigDecimal("100"));
		
		BigDecimal efeitoCompostoJurosAoLongoTempo = BigDecimal.ONE.add(taxaAoMesPercentual).pow(numeroParcelas);
		BigDecimal parte2 = taxaAoMesPercentual.multiply(efeitoCompostoJurosAoLongoTempo);
		BigDecimal parte3 = efeitoCompostoJurosAoLongoTempo.subtract(BigDecimal.ONE);
		
		
		//int scale = (valorOriginal.intValue()+"").length() + casasDecimais;
		BigDecimal multiplicadorParaDescobrirParcela = parte2.divide(parte3, casasDecimais, arrendondamento);
		
		return valorOriginal.multiply(multiplicadorParaDescobrirParcela);

	}	
}
