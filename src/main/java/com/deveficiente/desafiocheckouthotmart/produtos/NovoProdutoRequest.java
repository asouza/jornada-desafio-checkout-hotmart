package com.deveficiente.desafiocheckouthotmart.produtos;

import com.deveficiente.desafiocheckouthotmart.contas.Conta;

import jakarta.validation.constraints.NotBlank;

public class NovoProdutoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String descricao;

    

    // getters and setters

    public NovoProdutoRequest(@NotBlank String nome, @NotBlank String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Produto toModel(Conta conta) {
        return new Produto(nome, descricao, conta);
    }
}
