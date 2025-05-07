package com.deveficiente.desafiocheckouthotmart.simuladores.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações para o comportamento dos simuladores
 */
@Configuration
@ConfigurationProperties(prefix = "simuladores")
public class SimulationConfiguration {
    
    private boolean habilitado = false;
    private SimulationMode mode = SimulationMode.NORMAL;
    private int baseDelayMs = 500;
    private int variationDelayMs = 1500;
    private int errorProbability = 5;
    private boolean simulateResourceContention = true;
    
    public enum SimulationMode {
        NORMAL,    // Comportamento normal
        REALISTIC, // Comportamento realístico com variações maiores
        CHAOS      // Modo caótico para testes de resiliência
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }
    
    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    public SimulationMode getMode() {
        return mode;
    }
    
    public void setMode(SimulationMode mode) {
        this.mode = mode;
    }
    
    public int getBaseDelayMs() {
        return baseDelayMs;
    }
    
    public void setBaseDelayMs(int baseDelayMs) {
        this.baseDelayMs = baseDelayMs;
    }
    
    public int getVariationDelayMs() {
        return variationDelayMs;
    }
    
    public void setVariationDelayMs(int variationDelayMs) {
        this.variationDelayMs = variationDelayMs;
    }
    
    public int getErrorProbability() {
        return errorProbability;
    }
    
    public void setErrorProbability(int errorProbability) {
        this.errorProbability = errorProbability;
    }
    
    public boolean isSimulateResourceContention() {
        return simulateResourceContention;
    }
    
    public void setSimulateResourceContention(boolean simulateResourceContention) {
        this.simulateResourceContention = simulateResourceContention;
    }
}