package com.deveficiente.desafiocheckouthotmart.contas;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.deveficiente.desafiocheckouthotmart.compartilhado.UniqueValue;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.fasterxml.jackson.annotation.JsonCreator;

public class NovaContaRequest {

    @NotBlank
    @Email
    @UniqueValue(domainClass = Conta.class, fieldName = "email")
    private String email;
    

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public NovaContaRequest(@NotBlank @Email String email) {
        this.email = email;
    }


    public Conta toModel(@NotNull Configuracao configuracao) {
        return new Conta(email, configuracao);
    }
}
