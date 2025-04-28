package com.deveficiente.desafiocheckouthotmart.featureflag;

public class FeatureFlagStatusResponse {
    
    private final boolean habilitada;
    
    public FeatureFlagStatusResponse(boolean habilitada) {
        this.habilitada = habilitada;
    }
    
    public boolean isHabilitada() {
        return habilitada;
    }
}
