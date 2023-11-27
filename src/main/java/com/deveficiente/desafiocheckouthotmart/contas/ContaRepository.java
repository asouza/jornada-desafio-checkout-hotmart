package com.deveficiente.desafiocheckouthotmart.contas;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long>{

    Optional<Conta> findByCodigo(UUID codigo);

	Optional<Conta> findByEmail(String email);

}
