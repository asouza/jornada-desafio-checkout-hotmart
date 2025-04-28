package com.deveficiente.desafiocheckouthotmart.featureflag;

public class NovaFeatureFlagResponse {
    
    private final String codigo;
    
    public NovaFeatureFlagResponse(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
}
