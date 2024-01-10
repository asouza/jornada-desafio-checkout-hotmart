package com.deveficiente.desafiocheckouthotmart.cupom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

class CupomTest {

	@DisplayName("deveria calcular um valor para um percentual de desconto")
	@CsvSource({ "50,50", "10,90", "23,77" })
	@ParameterizedTest
	void test(BigDecimal percentual, BigDecimal valorEsperado) {
		Configuracao configuracao = new Configuracao(BigDecimal.TEN,
				BigDecimal.TEN, true);
		Conta conta = new Conta("email@email.com", configuracao);
		Produto produto = new Produto("teste", "descricao", conta);
		Oferta oferta = produto.adicionaOferta(produtoEmQuestao -> {
			return new Oferta(produtoEmQuestao, "nomeOferta",
					new BigDecimal("100"), 10, QuemPagaJuros.cliente);
		}).getSuccessReturn();

		Cupom cupom = new Cupom(produto, "teste", percentual,
				LocalDateTime.now().plusDays(3));

		Assertions.assertEquals(valorEsperado,
				cupom.aplicaDesconto(oferta).setScale(0));
	}

}
