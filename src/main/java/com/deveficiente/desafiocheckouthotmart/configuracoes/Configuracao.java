package com.deveficiente.desafiocheckouthotmart.configuracoes;

import java.math.BigDecimal;

import org.springframework.util.Assert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @Positive @NotNull BigDecimal taxaComissao;
    private @Positive @NotNull BigDecimal taxaJuros;
    private @NotNull boolean opcaoDefault;

    @Deprecated
    Configuracao(){

    }

    public Configuracao(@Positive @NotNull BigDecimal taxaComissao, @Positive @NotNull BigDecimal taxaJuros,
            @NotNull Boolean opcaoDefault) {
                
                //#copilotGerou
                Assert.isTrue(taxaComissao.compareTo(BigDecimal.ZERO) > 0,"A taxa de comissao precisa ser maior que zero");
                //#copilotGerou
                Assert.isTrue(taxaJuros.compareTo(BigDecimal.ZERO) > 0,"A taxa de juros precisa ser maior que zero");
                //#copilotGerou
                Assert.notNull(opcaoDefault,"A opcao default nao pode ser nula");

                this.taxaComissao = taxaComissao;
                this.taxaJuros = taxaJuros;
                this.opcaoDefault = opcaoDefault;
    }

}
