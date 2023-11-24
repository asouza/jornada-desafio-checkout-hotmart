package com.deveficiente.desafiocheckouthotmart.produtos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	Optional<Produto> findByCodigo(UUID codigo);

}
