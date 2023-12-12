package com.deveficiente.desafiocheckouthotmart.checkout;

public enum StatusCompra {

	//sera que tinha ficar tudo aqui? isso vai me puxar pelo pe?
	/*
	 * o script de geracao de tabela do hibernate ta gerando
	 * alguma coisa no h2 que trava a coluna para só receber os valores
	 * de enum que existem naquele momento. A coluna aparece como tipo
	 * varchar, então de fato eu não sei. 
	 */
	iniciada,finalizada, gerando_boleto, boleto_gerado
}
