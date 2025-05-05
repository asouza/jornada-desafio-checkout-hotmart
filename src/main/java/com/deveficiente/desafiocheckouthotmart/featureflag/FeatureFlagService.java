package com.deveficiente.desafiocheckouthotmart.featureflag;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao.FluxoRealizacaoCompraCartao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagService {
    
    private final FeatureFlagClient featureFlagClient;
    private static final Logger log = LoggerFactory
            .getLogger(FeatureFlagService.class);
    
    public FeatureFlagService(FeatureFlagClient featureFlagClient) {
        this.featureFlagClient = featureFlagClient;
    }
    
    /**
     * Verifica se uma feature flag está habilitada
     * 
     * @param codigo Código da feature flag
     * @return true se a feature estiver habilitada, false caso contrário
     */
    public boolean isFeatureHabilitada(String codigo) {
        try {
            Log5WBuilder
                    .metodo()
                    .oQueEstaAcontecendo("Consultando servico de feature flag")
                    .adicionaInformacao("codigo",codigo)
                    .info(log);

            ResponseEntity<FeatureFlagStatusResponse> response = featureFlagClient.verificarStatus(codigo);

            Log5WBuilder
                    .metodo()
                    .oQueEstaAcontecendo("Resposta: Consultando servico de feature flag")
                    .adicionaInformacao("codigo",codigo)
                    .adicionaInformacao("status",response.getBody().isHabilitada()+"")
                    .info(log);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().isHabilitada();
            }
            
            return false;
        } catch (Exception e) {
            Log5WBuilder
                    .metodo()
                    .oQueEstaAcontecendo("Aconteceu um problema na checagem de feature flag")
                    .adicionaInformacao("codigo",codigo)
                    .erro(log,e);
            //sendo conservador e retornando false. Aqui poderia ser configurável também. 
            return false;
        }
    }
}