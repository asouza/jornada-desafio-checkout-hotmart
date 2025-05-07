package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationConfiguration;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationOrchestrator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulador local do Gateway 1 de pagamento com cartão que emula
 * o comportamento realístico de um serviço de pagamento externo.
 */
@RestController
@RequestMapping("/gateway")
public class SimuladorGateway1CartaoController {
    
    private static final Logger log = LoggerFactory.getLogger(SimuladorGateway1CartaoController.class);
    private static final String SERVICE_NAME = "gateway1";
    
    @Autowired
    private SimulationOrchestrator simulationOrchestrator;
    
    @Autowired
    private SimulationConfiguration configuration;
    
    @PostMapping("/payments")
    public Map<String, Object> executa(@RequestBody NovoPagamentoGatewayCartao1Request paymentRequest) {
        log.debug("Recebida solicitação de pagamento no gateway1");
        
        // Utiliza o orquestrador para executar a lógica do simulador com todas as condições realísticas
        return executeWithSimulation(paymentRequest);
    }
    
    private Map<String, Object> executeWithSimulation(NovoPagamentoGatewayCartao1Request paymentRequest) {
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
    
    private Map<String, Object> createPaymentResponse(NovoPagamentoGatewayCartao1Request paymentRequest) {
        // Gera uma resposta simulada
        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", UUID.randomUUID().toString());
        response.put("status", "approved");
        response.put("authorizationCode", "AUTH" + System.currentTimeMillis() % 100000);
        response.put("cardInfo", paymentRequest.getNumeroCartao().substring(0, 4) + "********" + 
                    paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        response.put("installments", paymentRequest.getNumeroParcelas());
        response.put("processingTime", System.currentTimeMillis());
        response.put("gateway", SERVICE_NAME);
        
        log.debug("Resposta gerada para transação no {}", SERVICE_NAME);
        return response;
    }
}