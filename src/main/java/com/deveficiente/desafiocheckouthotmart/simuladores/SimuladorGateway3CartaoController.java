package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao.NovoPagamentoGatewayCartao3Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulador local do Gateway 3 de pagamento com cartão
 */
@RestController
@RequestMapping("/gateway-3")
public class SimuladorGateway3CartaoController {
    
    @PostMapping("/payments")
    public Map<String, Object> executa(@RequestBody NovoPagamentoGatewayCartao3Request paymentRequest) {
        // Simula um atraso randômico
        RandomDelaySimulator.executeWithLoadVariation(10); // 10% de chance de atraso maior (gateway mais rápido)
        
        // Simula a resposta que seria retornada pelo serviço real
        Map<String, Object> response = new HashMap<>();
        response.put("transaction", UUID.randomUUID().toString());
        response.put("outcome", "approved");
        response.put("authorization", "G3X" + System.currentTimeMillis() % 10000);
        response.put("cardNumber", "****-****-****-" + 
                    paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        response.put("installmentCount", paymentRequest.getNumeroParcelas());
        response.put("gatewayName", "gateway3");
        response.put("processingTime", System.currentTimeMillis());
        
        return response;
    }
}