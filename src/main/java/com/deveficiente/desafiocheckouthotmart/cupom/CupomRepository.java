package com.deveficiente.desafiocheckouthotmart.cupom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CupomRepository extends JpaRepository<Cupom, Long> {

	Optional<Cupom> findByCodigoAndProdutoId(String codigo,Long produtoId);

}
