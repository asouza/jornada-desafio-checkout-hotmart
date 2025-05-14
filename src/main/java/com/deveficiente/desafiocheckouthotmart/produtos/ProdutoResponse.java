package com.deveficiente.desafiocheckouthotmart.produtos;

import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO para representar dados básicos de um produto e suas ofertas na listagem
 */
public class ProdutoResponse {

    private final String nome;
    private final UUID codigo;
    private final List<OfertaResponse> ofertas;

    public ProdutoResponse(Produto produto) {
        this.nome = produto.getNome();
        this.codigo = produto.getCodigo();
        this.ofertas = criaOfertasResponse(produto);
    }

    private List<OfertaResponse> criaOfertasResponse(Produto produto) {
        return produto.getOfertas().stream()
                .map(OfertaResponse::new)
                .collect(Collectors.toList());
    }

    public String getNome() {
        return nome;
    }

    public UUID getCodigo() {
        return codigo;
    }

    public List<OfertaResponse> getOfertas() {
        return ofertas;
    }
}