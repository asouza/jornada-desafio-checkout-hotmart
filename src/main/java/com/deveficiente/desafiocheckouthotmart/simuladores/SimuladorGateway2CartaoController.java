package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao.NovoPagamentoGatewayCartao2Request;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.ResourceContentionEmulator;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationConfiguration;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationOrchestrator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Simulador local avançado do Gateway 2 de pagamento com cartão.
 * Este simulador implementa comportamento realístico de um serviço externo,
 * incluindo simulação de latência, contenção de recursos e falhas ocasionais.
 */
@RestController
@RequestMapping("/gateway-2")
public class SimuladorGateway2CartaoController {
    
    private static final Logger log = LoggerFactory.getLogger(SimuladorGateway2CartaoController.class);
    private static final String SERVICE_NAME = "gateway2";
    private static final Random random = new Random();
    
    @Autowired
    private SimulationOrchestrator simulationOrchestrator;
    
    @Autowired
    private ResourceContentionEmulator contentionEmulator;
    
    @Autowired
    private SimulationConfiguration configuration;
    
    @PostMapping("/payments")
    public String executa(@RequestBody NovoPagamentoGatewayCartao2Request paymentRequest) {
        log.debug("Recebida solicitação de pagamento no gateway2 para cartão: {}*****{}", 
                paymentRequest.getNumeroCartao().substring(0, 4),
                paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        
        // Gateway 2 tem seu próprio mecanismo de validação que consome tempo
        preprocessRequest(paymentRequest);
        
        // Usa orquestrador para simular comportamento da integração
        return executeWithSimulation(paymentRequest).get("transactionId").toString();
    }
    
    /**
     * Este método simula pré-processamento específico do Gateway 2 que valida
     * o cartão antes de processar o pagamento, adicionando complexidade e atraso.
     */
    private void preprocessRequest(NovoPagamentoGatewayCartao2Request request) {
        try {
            // Simulação de validação interna do gateway que consome tempo
            int chance = random.nextInt(100);
            
            if (chance < 40) {  // 40% das vezes faz validação pesada
                // Esta parte é difícil de rastrear pois está embutida aqui
                contentionEmulator.simulateResourceContention();
                // Aumenta o tempo de validação com alto desvio padrão
                TimeUnit.MILLISECONDS.sleep(500 + random.nextInt(2500));
            } else if (chance < 60) { // 20% das vezes tem comportamento errático
                // Introduz comportamento errático onde às vezes responde rápido, às vezes muito lento
                if (random.nextBoolean()) {
                    TimeUnit.MILLISECONDS.sleep(10); // Muito rápido
                } else {
                    TimeUnit.MILLISECONDS.sleep(4000 + random.nextInt(4000)); // Muito lento
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private Map<String, Object> executeWithSimulation(NovoPagamentoGatewayCartao2Request paymentRequest) {
        // Introduz falhas aleatórias
        int randomValue = random.nextInt(100);
        
        // 15% de timeout completo
        if (randomValue < 15) {
            try {
                log.warn("Gateway 2 simulando timeout longo");
                Thread.sleep(31000); // Timeout maior que o padrão de 30s
                throw new RuntimeException("Timeout Exceeded");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 20% de erro 503 Service Unavailable
        if (randomValue >= 15 && randomValue < 35) {
            log.error("Gateway 2 retornando erro 503");
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE, "Gateway temporarily unavailable");
        }
        
        // Escolhe o modo de simulação baseado na configuração para os 65% restantes
        switch (configuration.getMode()) {
            case CHAOS:
                return simulationOrchestrator.executeChaosOperation(SERVICE_NAME, 
                        () -> createPaymentResponse(paymentRequest));
                
            case REALISTIC:
            case NORMAL:
            default:
                return simulationOrchestrator.executeServiceOperation(SERVICE_NAME, 
                        () -> createPaymentResponse(paymentRequest));
        }
    }
    
    private Map<String, Object> createPaymentResponse(NovoPagamentoGatewayCartao2Request paymentRequest) {
        // Cria resposta simulada com formato específico do Gateway 2
        Map<String, Object> response = new HashMap<>();
        response.put("payment_id", UUID.randomUUID().toString());
        response.put("result", "success");
        response.put("auth_code", "G2-" + System.currentTimeMillis() % 100000);
        response.put("card_masked", paymentRequest.getNumeroCartao().substring(0, 6) + "******" + 
                    paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        response.put("payment_installments", paymentRequest.getNumeroParcelas());
        response.put("provider", "gateway2");
        response.put("timestamp", System.currentTimeMillis());
        response.put("transactionId", UUID.randomUUID());
        
        // Gateway 2 inclui informações adicionais de processamento
        Map<String, Object> processingDetails = new HashMap<>();
        processingDetails.put("processor_id", "GATEWAY2-" + random.nextInt(999999));
        processingDetails.put("authorization_flow", "standard");
        processingDetails.put("security_checks_passed", true);
        response.put("processing_details", processingDetails);
        
        log.debug("Resposta gerada para transação no {}", SERVICE_NAME);
        return response;
    }
}