package com.deveficiente.desafiocheckouthotmart.featureflag;

import com.fasterxml.jackson.annotation.JsonCreator;

public class FeatureFlagStatusResponse {
    
    private final boolean habilitada;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FeatureFlagStatusResponse(boolean habilitada) {
        this.habilitada = habilitada;
    }
    
    public boolean isHabilitada() {
        return habilitada;
    }
}
