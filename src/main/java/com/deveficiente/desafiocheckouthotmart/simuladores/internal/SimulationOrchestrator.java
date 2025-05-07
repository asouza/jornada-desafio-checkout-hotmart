package com.deveficiente.desafiocheckouthotmart.simuladores.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Orquestrador central para simulações que coordena diversos aspectos
 * como condições de rede, latência, perfis de carga e comportamentos de falha.
 */
@Component
@Primary
public class SimulationOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(SimulationOrchestrator.class);
    private static final Random random = new Random();
    
    @Autowired
    private LoadProfileManager loadProfileManager;
    
    @Autowired
    private NetworkConditionSimulator networkSimulator;
    
    @Autowired
    private ResourceContentionEmulator contentionEmulator;
    
    /**
     * Executa uma operação com todo o realismo de um serviço externo,
     * incluindo latência, jitter, falhas e contenção de recursos.
     * 
     * @param serviceName nome do serviço sendo simulado
     * @param task tarefa a ser executada
     * @param <T> tipo de retorno da tarefa
     * @return resultado da operação
     */
    public <T> T executeServiceOperation(String serviceName, Supplier<T> task) {
        log.debug("Executing simulation for service: {}", serviceName);
        
        // Primeiro, aplica o atraso baseado no perfil do serviço
        loadProfileManager.applyServiceLatency(serviceName);
        
        // Depois, simula contenção de recursos (afeta o CPU)
        contentionEmulator.simulateResourceContention();
        
        // Por fim, executa a operação com condições de rede simuladas
        try {
            CompletableFuture<T> future = networkSimulator.executeAsync(task);
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulation interrupted", e);
        } catch (ExecutionException e) {
            // Transforma em RuntimeException para simplificar a API
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RuntimeException("Error during simulation execution", e.getCause());
        }
    }
    
    /**
     * Executa uma operação com comportamento caótico para testes de resiliência
     * 
     * @param serviceName nome do serviço
     * @param task tarefa a executar
     * @param <T> tipo de retorno
     * @return resultado
     */
    public <T> T executeChaosOperation(String serviceName, Supplier<T> task) {
        // Adiciona comportamento caótico com probabilidade de erro maior
        if (random.nextInt(100) < 30) {  // 30% de chance de erro
            log.warn("Chaos mode triggered for service: {}", serviceName);
            throw new RuntimeException("Chaos simulation: random failure for " + serviceName);
        }
        
        // Se não falhar, executa normalmente mas com latência maior
        try {
            Thread.sleep(1000 + random.nextInt(4000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return task.get();
    }
}