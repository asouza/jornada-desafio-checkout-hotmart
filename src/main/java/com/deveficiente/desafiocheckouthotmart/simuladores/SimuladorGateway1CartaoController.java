package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulador local do Gateway 1 de pagamento com cartão
 */
@RestController
@RequestMapping("/gateway")
public class SimuladorGateway1CartaoController {
    
    @PostMapping("/payments")
    public Map<String, Object> executa(@RequestBody NovoPagamentoGatewayCartao1Request paymentRequest) {
        // Simula um atraso randômico
        RandomDelaySimulator.executeWithLoadVariation(15); // 15% de chance de atraso maior
        
        // Simula a resposta que seria retornada pelo serviço real
        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", UUID.randomUUID().toString());
        response.put("status", "approved");
        response.put("authorizationCode", "AUTH" + System.currentTimeMillis() % 100000);
        response.put("cardInfo", paymentRequest.getNumeroCartao().substring(0, 4) + "********" + 
                    paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        response.put("installments", paymentRequest.getNumeroParcelas());
        response.put("gateway", "gateway1");
        
        return response;
    }
}