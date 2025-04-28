package com.deveficiente.desafiocheckouthotmart.featureflag;

public class JaExisteFeatureFlagComMesmoCodigoException extends RuntimeException {
    public JaExisteFeatureFlagComMesmoCodigoException(String codigo) {
        super("Já existe uma feature flag com o código: " + codigo);
    }
}
