package com.deveficiente.desafiocheckouthotmart.produtos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para listagem de produtos com suas ofertas
 */
@RestController
@RequestMapping("/api/produtos")
public class ListaProdutosController {

    private static final Logger logger = LoggerFactory.getLogger(ListaProdutosController.class);
    
    private final ProdutoRepository produtoRepository;
    
    public ListaProdutosController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }
    
    /**
     * Lista todos os produtos com suas ofertas de forma paginada
     * 
     * @param pageable configuração de paginação (tamanho da página, ordenação, etc)
     * @return página contendo produtos e suas ofertas
     */
    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        
        logger.debug("Listando produtos com paginação: {}", pageable);
        
        Page<Produto> paginaProdutos = produtoRepository.findAll(pageable);
        Page<ProdutoResponse> paginaResponse = paginaProdutos.map(ProdutoResponse::new);
        
        return ResponseEntity.ok(paginaResponse);
    }
}