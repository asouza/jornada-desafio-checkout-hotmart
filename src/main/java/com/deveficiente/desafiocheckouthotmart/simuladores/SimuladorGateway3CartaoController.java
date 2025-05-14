package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao.NovoPagamentoGatewayCartao3Request;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.NetworkConditionSimulator;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationConfiguration;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationOrchestrator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Simulador avançado do Gateway 3 de pagamento com cartão.
 * Este gateway implementa um processamento paralelo e assíncrono,
 * tornando mais complexo o rastreamento de atrasos.
 */
@RestController
@RequestMapping("/gateway-3")
public class SimuladorGateway3CartaoController {
    
    private static final Logger log = LoggerFactory.getLogger(SimuladorGateway3CartaoController.class);
    private static final String SERVICE_NAME = "gateway3";
    private static final Random random = new Random();
    
    @Autowired
    private SimulationOrchestrator simulationOrchestrator;
    
    @Autowired
    private NetworkConditionSimulator networkSimulator;
    
    @Autowired
    private SimulationConfiguration configuration;
    
    @PostMapping("/payments")
    public String executa(@RequestBody NovoPagamentoGatewayCartao3Request paymentRequest) {
        log.debug("Recebida solicitação de pagamento no gateway3");
        
        // Gateway 3 usa processamento assíncrono internamente
        return processPaymentAsynchronously(paymentRequest).get("transactionId").toString();
    }
    
    /**
     * Implementa um fluxo assíncrono de processamento de pagamento que
     * divide a operação em várias etapas paralelas, tornando difícil
     * identificar a fonte exata do atraso.
     */
    private Map<String, Object> processPaymentAsynchronously(NovoPagamentoGatewayCartao3Request paymentRequest) {
        try {
            // Simula o pipeline de processamento assíncrono do gateway3
            List<CompletableFuture<Void>> processingSteps = new ArrayList<>();
            
            // Etapa 1: Validação do cartão
            CompletableFuture<Void> cardValidation = networkSimulator.executeAsync(() -> {
                // Esta etapa fica escondida dentro deste fluxo assíncrono
                sleepRandomly(100, 300);
                return null;
            });
            processingSteps.add(cardValidation);
            
            // Etapa 2: Verificação de fraude
            CompletableFuture<Void> fraudCheck = networkSimulator.executeAsync(() -> {
                if (random.nextInt(100) < 35) { // 35% das vezes faz verificação mais profunda
                    sleepRandomly(400, 800);
                } else {
                    sleepRandomly(50, 200);
                }
                return null;
            });
            processingSteps.add(fraudCheck);
            
            // Etapa 3: Verificação de limite
            CompletableFuture<Void> limitCheck = cardValidation.thenComposeAsync(result -> 
                networkSimulator.executeAsync(() -> {
                    sleepRandomly(50, 150);
                    return null;
                })
            );
            processingSteps.add(limitCheck);
            
            // Aguarda todas as etapas concluírem
            CompletableFuture.allOf(processingSteps.toArray(new CompletableFuture[0])).get();
            
            // Fase final de processamento usa o orquestrador
            return executeWithSimulation(paymentRequest);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processamento de pagamento interrompido", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro no processamento assíncrono do pagamento", e.getCause());
        }
    }
    
    private void sleepRandomly(int minMs, int maxMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(minMs + random.nextInt(maxMs - minMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private Map<String, Object> executeWithSimulation(NovoPagamentoGatewayCartao3Request paymentRequest) {
        // Escolhe o modo de simulação baseado na configuração
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
    
    private Map<String, Object> createPaymentResponse(NovoPagamentoGatewayCartao3Request paymentRequest) {
        // Cria resposta simulada com formato específico do Gateway 3
        Map<String, Object> response = new HashMap<>();
        String transactionId = UUID.randomUUID().toString();
        response.put("transactionId", transactionId);
        response.put("outcome", "approved");
        response.put("authorization", "G3X" + System.currentTimeMillis() % 10000);
        response.put("cardNumber", "****-****-****-" + 
                   paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        response.put("installmentCount", paymentRequest.getNumeroParcelas());
        response.put("gatewayName", "gateway3");
        response.put("processingTime", System.currentTimeMillis());
        
        // Gateway 3 inclui informações detalhadas sobre o processamento
        Map<String, Object> details = new HashMap<>();
        details.put("riskScore", random.nextInt(100));
        details.put("processingNetwork", "G3-NETWORK");
        details.put("transactionType", "PURCHASE");
        details.put("processingFee", new BigDecimal("0.0" + (random.nextInt(40) + 10)));
        
        // Etapas de processamento com timestamps que consomem a maior parte do tempo
        Map<String, Object> processingSteps = new HashMap<>();
        processingSteps.put("validation", System.currentTimeMillis() - random.nextInt(500));
        processingSteps.put("riskAnalysis", System.currentTimeMillis() - random.nextInt(300));
        processingSteps.put("authorization", System.currentTimeMillis() - random.nextInt(200));
        processingSteps.put("settlement", System.currentTimeMillis());
        
        details.put("processingSteps", processingSteps);
        response.put("details", details);
        
        log.debug("Resposta gerada para transação {} no {}", transactionId, SERVICE_NAME);
        return response;
    }
}