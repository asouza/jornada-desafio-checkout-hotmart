package com.deveficiente.desafiocheckouthotmart.featureflag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "feature_flags")
public class FeatureFlag {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String codigo;
    
    @NotNull
    private boolean habilitada;
    
    /**
     * @deprecated uso exclusivo do Hibernate
     */
    @Deprecated
    public FeatureFlag() {
    }
    
    public FeatureFlag(@NotBlank String codigo, @NotNull boolean habilitada) {
        this.codigo = codigo;
        this.habilitada = habilitada;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public boolean isHabilitada() {
        return habilitada;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlag that = (FeatureFlag) o;
        return Objects.equals(codigo, that.codigo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
