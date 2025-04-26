package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao.NovoPagamentoGatewayCartao2Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulador local do Gateway 2 de pagamento com cartão
 */
@RestController
@RequestMapping("/gateway-2")
public class SimuladorGateway2CartaoController {
    
    @PostMapping("/payments")
    public Map<String, Object> executa(@RequestBody NovoPagamentoGatewayCartao2Request paymentRequest) {
        // Simula um atraso randômico
        RandomDelaySimulator.executeWithLoadVariation(25); // 25% de chance de atraso maior (gateway mais lento)
        
        // Simula a resposta que seria retornada pelo serviço real
        Map<String, Object> response = new HashMap<>();
        response.put("payment_id", UUID.randomUUID().toString());
        response.put("result", "success");
        response.put("auth_code", "G2-" + System.currentTimeMillis() % 100000);
        response.put("card_masked", paymentRequest.getNumeroCartao().substring(0, 6) + "******" + 
                    paymentRequest.getNumeroCartao().substring(paymentRequest.getNumeroCartao().length() - 4));
        response.put("payment_installments", paymentRequest.getNumeroParcelas());
        response.put("provider", "gateway2");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
}