package com.deveficiente.desafiocheckouthotmart.simuladores;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para habilitar ou desabilitar os simuladores de serviços externos.
 * Os simuladores são ativados quando a propriedade simuladores.habilitado é true.
 */
@Configuration
public class SimuladoresConfig {

    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorBoletoApiController simuladorBoletoApiController() {
        return new SimuladorBoletoApiController();
    }
    
    @Bean
    @ConditionalOnProperty(name = "simuladores.habilitado", havingValue = "true", matchIfMissing = false)
    public SimuladorGateway1CartaoController simuladorGateway1CartaoController() {
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