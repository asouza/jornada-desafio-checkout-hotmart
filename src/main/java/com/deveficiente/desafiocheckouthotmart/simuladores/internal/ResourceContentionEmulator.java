package com.deveficiente.desafiocheckouthotmart.simuladores.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Emula contenção de recursos do sistema como CPU, memória e IO
 * para tornar os simuladores mais realistas.
 */
@Component
public class ResourceContentionEmulator {
    
    private static final Logger log = LoggerFactory.getLogger(ResourceContentionEmulator.class);
    private static final Random random = new Random();
    
    // Probabilidades para cada tipo de contenção
    private static final int CPU_CONTENTION_PROBABILITY = 10; // 10%
    private static final int MEMORY_CONTENTION_PROBABILITY = 5; // 5%
    private static final int IO_CONTENTION_PROBABILITY = 3; // 3%
    
    /**
     * Simula contenção de recursos com probabilidades configuráveis
     */
    public void simulateResourceContention() {
        // Simula contenção de CPU
        if (random.nextInt(100) < CPU_CONTENTION_PROBABILITY) {
            simulateCpuContention();
        }
        
        // Simula contenção de memória
        if (random.nextInt(100) < MEMORY_CONTENTION_PROBABILITY) {
            simulateMemoryContention();
        }
        
        // Simula contenção de I/O
        if (random.nextInt(100) < IO_CONTENTION_PROBABILITY) {
            simulateIoContention();
        }
    }
    
    /**
     * Simula alta utilização de CPU realizando cálculos intensivos
     */
    private void simulateCpuContention() {
        log.debug("Simulating CPU contention");
        int iterations = 5000 + random.nextInt(30000);
        
        // Realiza cálculos intensivos para consumir CPU
        double result = 0;
        for (int i = 0; i < iterations; i++) {
            result += Math.sin(random.nextDouble()) * Math.cos(random.nextDouble());
        }
        
        // Apenas para evitar que o compilador elimine o código
        if (result == Double.MAX_VALUE) {
            log.trace("CPU contention result: {}", result);
        }
    }
    
    /**
     * Simula pressão de memória alocando objetos temporários
     */
    private void simulateMemoryContention() {
        log.debug("Simulating memory contention");
        int size = 500000 + random.nextInt(1500000);
        
        // Aloca memória temporária
        List<byte[]> memoryBlocks = new ArrayList<>();
        try {
            for (int i = 0; i < 5; i++) {
                memoryBlocks.add(new byte[size]);
            }
            // Pequena pausa para manter os objetos vivos
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Limpa referências para permitir GC
            memoryBlocks.clear();
        }
    }
    
    /**
     * Simula contenção de I/O com operações de escrita/leitura dummy
     */
    private void simulateIoContention() {
        log.debug("Simulating I/O contention");
        try {
            // Simula atraso de I/O
            Thread.sleep(200 + random.nextInt(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}