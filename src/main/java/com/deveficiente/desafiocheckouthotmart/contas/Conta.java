package com.deveficiente.desafiocheckouthotmart.contas;

import java.util.UUID;

import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private @NotBlank @Email String email;
    @NotNull
    @ManyToOne
    private Configuracao configuracao;
    @NotNull
    private UUID codigo;

    public Conta(@NotBlank @Email String email, Configuracao configuracao) {
        this.email = email;
        this.configuracao = configuracao;
        this.codigo = UUID.randomUUID();
    }

}
