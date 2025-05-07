package com.deveficiente.desafiocheckouthotmart.simuladores.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Simula condições de rede, incluindo latência, jitter e perda de pacotes
 * para emular comportamentos realistas de serviços externos.
 */
@Component
public class NetworkConditionSimulator {
    
    private static final Logger log = LoggerFactory.getLogger(NetworkConditionSimulator.class);
    private static final Random random = new Random();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final JitterManager jitterManager = new JitterManager();
    private final PacketLossSimulator packetLossSimulator = new PacketLossSimulator();
    
    /**
     * Executa uma operação com simulação de condições de rede variáveis
     * 
     * @param task tarefa a ser executada
     * @param <T> tipo de retorno da tarefa
     * @return resultado da tarefa
     */
    public <T> T executeWithNetworkConditions(Supplier<T> task) {
        // Verifica se deve simular perda de pacote
        if (packetLossSimulator.shouldSimulatePacketLoss()) {
            throw new RuntimeException("Simulated network error: packet loss");
        }
        
        // Executa a tarefa após simular jitter
        jitterManager.simulateJitter();
        
        return task.get();
    }
    
    /**
     * Executa uma operação de forma assíncrona com simulação de rede
     * 
     * @param task tarefa a ser executada
     * @param <T> tipo de retorno da tarefa
     * @return CompletableFuture com o resultado da tarefa
     */
    public <T> CompletableFuture<T> executeAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("Executing async task with network simulation");
            try {
                // Adiciona um pequeno atraso aleatório para simular escalonamento da tarefa
                Thread.sleep(random.nextInt(50));
                return executeWithNetworkConditions(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task interrupted", e);
            }
        }, executor);
    }
    
    /**
     * Classe interna que gerencia a simulação de jitter (variação na latência)
     */
    private static class JitterManager {
        private static final int BASE_JITTER = 25; // ms
        private static final int MAX_JITTER_VARIATION = 75; // ms
        
        public void simulateJitter() {
            try {
                int jitterAmount = BASE_JITTER + random.nextInt(MAX_JITTER_VARIATION);
                Thread.sleep(jitterAmount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Classe interna que simula perda de pacotes na rede
     */
    private static class PacketLossSimulator {
        private static final int PACKET_LOSS_PROBABILITY = 3; // 3%
        
        public boolean shouldSimulatePacketLoss() {
            return random.nextInt(100) < PACKET_LOSS_PROBABILITY;
        }
    }
}