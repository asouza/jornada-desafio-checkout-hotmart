package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples.NovoBoletoRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simulador local do serviço de boletos que implementa a mesma interface
 * que o BoletoApiClient se comunica remotamente.
 */
@RestController
@RequestMapping("/boletos")
public class SimuladorBoletoApiController {
    
    @PostMapping("/new")
    public Map<String, Object> executa(@RequestBody NovoBoletoRequest paymentRequest) {
        // Simula um atraso randômico
        RandomDelaySimulator.executeWithLoadVariation(20); // 20% de chance de atraso maior
        
        // Simula a resposta que seria retornada pelo serviço real
        Map<String, Object> response = new HashMap<>();
        response.put("id", UUID.randomUUID().toString());
        response.put("status", "success");
        response.put("codigo", paymentRequest.getCodigo());
        response.put("valor", paymentRequest.getValor());
        response.put("url", "http://localhost:8080/download/boleto/" + paymentRequest.getCodigo());
        
        return response;
    }
}