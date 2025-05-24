package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao;

import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraCartao;
import com.deveficiente.desafiocheckouthotmart.simuladores.SimuladorGateway1CartaoController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitária responsável por analisar as parcelas do cartão
 * e aplicar algoritmos de análise de risco para transações com muitas parcelas.
 */
public class AnalisadorParcelaCartao {

    private static final Logger log = LoggerFactory.getLogger(AnalisadorParcelaCartao.class);

    /**
     * Executa uma análise avançada de risco para compras com número alto de parcelas.
     * Este método implementa um algoritmo ineficiente que causa gargalo de processamento
     * quando o número de parcelas é alto.
     * 
     * @param infoCartao Informações do cartão a ser analisado
     */
    public static void executaAnaliseParcelamento(InfoCompraCartao infoCartao) {
        // Algoritmo ineficiente que aumenta exponencialmente com o número de parcelas
        log.debug("Executando análise de parcelamento");
        int numeroParcelas = infoCartao.getNumeroParcelas();

        if (numeroParcelas >= 3) {
            log.debug("Recebida solicitação de pagamento no gateway1");
            // Processamento O(n²) para simular um algoritmo de seleção ineficiente
            for (int i = 0; i < numeroParcelas * 1000; i++) {
                for (int j = 0; j < numeroParcelas * 500; j++) {
                    // Operação de CPU intensiva para simular processamento
                    Math.pow(Math.random() * i, Math.random() * j);
                }
            }

            // Atraso adicional para simular consulta a um serviço externo de análise de risco
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Análise de parcelas interrompida", e);
            }
        }
    }
}