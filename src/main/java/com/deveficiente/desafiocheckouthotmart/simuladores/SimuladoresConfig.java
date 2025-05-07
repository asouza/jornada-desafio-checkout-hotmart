package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.deveficiente.desafiocheckouthotmart.simuladores.internal.LoadProfileManager;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationConfiguration;
import com.deveficiente.desafiocheckouthotmart.simuladores.internal.SimulationOrchestrator;

import jakarta.annotation.PostConstruct;

/**
 * Configuração avançada para simuladores de serviços externos.
 * Os simuladores são ativados quando a propriedade simuladores.habilitado é true.
 */
@Configuration
public class SimuladoresConfig {
    
    private static final Logger log = LoggerFactory.getLogger(SimuladoresConfig.class);
    
    private final SimulationConfiguration simulationConfiguration;
    
    public SimuladoresConfig(SimulationConfiguration simulationConfiguration) {
        this.simulationConfiguration = simulationConfiguration;
    }
    
    @PostConstruct
    public void init() {
        log.info("Inicializando configuração de simuladores no modo: {}", 
                simulationConfiguration.getMode());
    }
    
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (simulationConfiguration.isHabilitado()) {
            log.info("Simuladores habilitados com as seguintes configurações:");
            log.info("  - Modo: {}", simulationConfiguration.getMode());
            log.info("  - Atraso base: {}ms", simulationConfiguration.getBaseDelayMs());
            log.info("  - Variação: {}ms", simulationConfiguration.getVariationDelayMs());
            log.info("  - Probabilidade de erro: {}%", simulationConfiguration.getErrorProbability());
            log.info("  - Simulação de contenção: {}", 
                    simulationConfiguration.isSimulateResourceContention() ? "Ativada" : "Desativada");
        } else {
            log.info("Simuladores desabilitados");
        }
    }

    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorBoletoApiController simuladorBoletoApiController() {
        return new SimuladorBoletoApiController();
    }
    
    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorGateway1CartaoController simuladorGateway1CartaoController(
            SimulationOrchestrator orchestrator, 
            SimulationConfiguration configuration) {
        return new SimuladorGateway1CartaoController();
    }
    
    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorGateway2CartaoController simuladorGateway2CartaoController() {
        return new SimuladorGateway2CartaoController();
    }
    
    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorGateway3CartaoController simuladorGateway3CartaoController() {
        return new SimuladorGateway3CartaoController();
    }
    
    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorProvider1EmailController simuladorProvider1EmailController() {
        return new SimuladorProvider1EmailController();
    }
}