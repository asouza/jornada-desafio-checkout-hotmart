package com.deveficiente.desafiocheckouthotmart.configuracoes;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NovaConfiguracaoRequest {

    @Positive
    @NotNull
    private BigDecimal taxaComissao;
    @Positive
    @NotNull    
    private BigDecimal taxaJuros;
    //deixa com tipo complexo para diminuir a chance de alguem definir default sem querer
    @NotNull
    private Boolean opcaoDefault;

    public NovaConfiguracaoRequest(BigDecimal taxaComissao, BigDecimal taxaJuros, Boolean opcaoDefault) {
        this.taxaComissao = taxaComissao;
        this.taxaJuros = taxaJuros;
        this.opcaoDefault = opcaoDefault;
    }

    public Configuracao toModel() {
        return new Configuracao(taxaComissao,taxaJuros,opcaoDefault);
    }

    
}
