package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompraRepository extends JpaRepository<Compra, Long> {

	@Query("select c from Compra c where c.metadados.infoCompraBoleto.codigoBoleto = :codigoBoleto")
	Optional<Compra> buscaPorCodigoBoleto(@UUID @Param("codigoBoleto") String codigoBoleto);

	@Query("select new com.deveficiente.desafiocheckouthotmart.checkout.CompraComTransacaoCarregada(c) from Compra c join fetch c.transacoes tx where tx.status = 'finalizada' and c.provisionamento is null")
	List<CompraComTransacaoCarregada> listaComprasNaoProvisionadas();

}
