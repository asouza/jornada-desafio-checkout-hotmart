package com.deveficiente.desafiocheckouthotmart.featureflag;

import com.deveficiente.desafiocheckouthotmart.compartilhado.UniqueValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NovaFeatureFlagRequest {
    
    @NotBlank
    @UniqueValue(domainClass = FeatureFlag.class, fieldName = "codigo")
    private final String codigo;
    
    @NotNull
    private final Boolean habilitada;
    
    @JsonCreator
    public NovaFeatureFlagRequest(String codigo, Boolean habilitada) {
        this.codigo = codigo;
        this.habilitada = habilitada;
    }
    
    public FeatureFlag toModel() {
        return new FeatureFlag(codigo, habilitada);
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public Boolean getHabilitada() {
        return habilitada;
    }
}
