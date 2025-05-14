package com.deveficiente.desafiocheckouthotmart.produtos;

import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para representar dados básicos de uma oferta na listagem de produtos
 */
public class OfertaResponse {

    private final String nome;
    private final UUID codigo;
    private final BigDecimal preco;
    private final boolean principal;

    public OfertaResponse(Oferta oferta) {
        this.nome = oferta.getNome();
        this.codigo = oferta.getCodigo();
        this.preco = oferta.getPreco();
        this.principal = oferta.isPrincipal();
    }

    public String getNome() {
        return nome;
    }

    public UUID getCodigo() {
        return codigo;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public boolean isPrincipal() {
        return principal;
    }
}