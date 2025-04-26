package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email.Provider1EmailRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulador local do serviço de envio de emails Provider1
 */
@RestController
@RequestMapping("/provider1")
public class SimuladorProvider1EmailController {
    
    @PostMapping("/emails")
    public Map<String, Object> sendEmail(@RequestBody Provider1EmailRequest emailRequest) {
        // Simula um atraso randômico
        RandomDelaySimulator.executeWithLoadVariation(30); // 30% de chance de atraso maior (serviço de email tende a variar mais)
        
        // Simula a resposta que seria retornada pelo serviço real
        Map<String, Object> response = new HashMap<>();
        String messageId = UUID.randomUUID().toString();
        
        response.put("messageId", messageId);
        response.put("status", "queued");
        response.put("recipient", emailRequest.getTo());
        response.put("sender", emailRequest.getFrom());
        response.put("subject", emailRequest.getSubject());
        response.put("queueTime", System.currentTimeMillis());
        response.put("estimatedDelivery", System.currentTimeMillis() + 5000); // Entrega estimada em 5 segundos
        
        return response;
    }
}