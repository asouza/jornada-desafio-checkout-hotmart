package com.deveficiente.desafiocheckouthotmart.simuladores.internal;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Gerenciador de perfis de carga para simulação de comportamentos de serviços externos.
 * Esta classe possui métodos para modificar dinamicamente o comportamento dos simuladores
 * com base em diferentes perfis de carga e latência de rede.
 */
@Component
public class LoadProfileManager {
    
    private static final Random random = new Random();
    
    // Mapa de configurações de latência por serviço
    private final Map<String, LatencyProfile> serviceLatencyProfiles = new HashMap<>();
    
    // Configurações padrão
    private static final LatencyProfile DEFAULT_PROFILE = new LatencyProfile(
            100, 2000, 15, 2000, 5000
    );
    
    public LoadProfileManager() {
        // Inicializa perfis padrão para cada serviço
        serviceLatencyProfiles.put("gateway1", DEFAULT_PROFILE);
        serviceLatencyProfiles.put("gateway2", DEFAULT_PROFILE);
        serviceLatencyProfiles.put("gateway3", DEFAULT_PROFILE);
        serviceLatencyProfiles.put("boleto", DEFAULT_PROFILE);
        serviceLatencyProfiles.put("email", DEFAULT_PROFILE);
    }
    
    /**
     * Aplica um atraso baseado no perfil de latência do serviço
     * 
     * @param serviceName nome do serviço para o qual aplicar o atraso
     */
    public void applyServiceLatency(String serviceName) {
        LatencyProfile profile = serviceLatencyProfiles.getOrDefault(serviceName, DEFAULT_PROFILE);
        
        try {
            if (random.nextInt(100) < profile.getHighLoadProbability()) {
                // Simula carga alta com atraso maior
                int highLoadDelay = random.nextInt(
                        profile.getMaxHighLoadDelay() - profile.getMinHighLoadDelay()) 
                        + profile.getMinHighLoadDelay();
                
                TimeUnit.MILLISECONDS.sleep(highLoadDelay);
            } else {
                // Carga normal
                int normalDelay = random.nextInt(
                        profile.getMaxNormalDelay() - profile.getMinNormalDelay())
                        + profile.getMinNormalDelay();
                
                TimeUnit.MILLISECONDS.sleep(normalDelay);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Retorna o perfil de latência para um serviço específico
     */
    public LatencyProfile getLatencyProfile(String serviceName) {
        return serviceLatencyProfiles.getOrDefault(serviceName, DEFAULT_PROFILE);
    }
    
    /**
     * Atualiza o perfil de latência para um serviço específico
     */
    public void updateLatencyProfile(String serviceName, LatencyProfile profile) {
        serviceLatencyProfiles.put(serviceName, profile);
    }
    
    /**
     * Classe interna que representa um perfil de latência para um serviço
     */
    public static class LatencyProfile {
        private final int minNormalDelay;
        private final int maxNormalDelay;
        private final int highLoadProbability;
        private final int minHighLoadDelay;
        private final int maxHighLoadDelay;
        
        public LatencyProfile(
                int minNormalDelay, 
                int maxNormalDelay, 
                int highLoadProbability,
                int minHighLoadDelay,
                int maxHighLoadDelay) {
            this.minNormalDelay = minNormalDelay;
            this.maxNormalDelay = maxNormalDelay;
            this.highLoadProbability = highLoadProbability;
            this.minHighLoadDelay = minHighLoadDelay;
            this.maxHighLoadDelay = maxHighLoadDelay;
        }
        
        public int getMinNormalDelay() {
            return minNormalDelay;
        }
        
        public int getMaxNormalDelay() {
            return maxNormalDelay;
        }
        
        public int getHighLoadProbability() {
            return highLoadProbability;
        }
        
        public int getMinHighLoadDelay() {
            return minHighLoadDelay;
        }
        
        public int getMaxHighLoadDelay() {
            return maxHighLoadDelay;
        }
    }
}