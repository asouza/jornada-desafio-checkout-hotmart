package com.deveficiente.desafiocheckouthotmart.simuladores;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Utilitário para simular atrasos randômicos em simuladores de APIs externas.
 * Usado para emular condições reais de rede e processamento em APIs remotas.
 */
public class RandomDelaySimulator {
    
    private static final Random random = new Random();
    
    /**
     * Simula um atraso randômico entre minDelay e maxDelay milissegundos
     * 
     * @param minDelay atraso mínimo em ms
     * @param maxDelay atraso máximo em ms
     */
    public static void execute(int minDelay, int maxDelay) {
        try {
            int delay = random.nextInt(maxDelay - minDelay) + minDelay;
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Simula um atraso randômico entre 100ms e 2000ms
     */
    public static void execute() {
        execute(100, 2000);
    }
    
    /**
     * Simula um atraso com variação de carga, onde existe uma chance 
     * configurável de o atraso ser muito maior que o normal
     * 
     * @param highLoadProbability probabilidade (0-100) de simular carga alta
     */
    public static void executeWithLoadVariation(int highLoadProbability) {
        if (random.nextInt(100) < highLoadProbability) {
            // Simula carga alta - atraso maior
            execute(2000, 5000);
        } else {
            // Carga normal
            execute(100, 1000);
        }
    }
}