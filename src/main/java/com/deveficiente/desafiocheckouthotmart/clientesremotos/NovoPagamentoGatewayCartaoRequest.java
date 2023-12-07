package com.deveficiente.desafiocheckouthotmart.clientesremotos;

import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;

public interface NovoPagamentoGatewayCartaoRequest {
	
	/*
	 * Vou deixar essa interface aqui para lembrar que criei pq imaginei
	 * fazer um double dispatch. Só que na prática, eu percebi que minha imaginação
	 * tinha falhado.
	 */

	public void preencheDados(String numeroCartao,
			String nomeTitular, String mes, int anoVencimento,
			ValorParcelaMes valorParcelaMes);

}
