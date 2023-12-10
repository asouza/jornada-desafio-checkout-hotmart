package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyPairRepository extends JpaRepository<IdempotencyKeyPair, Long>{

	Optional<IdempotencyKeyPair> findByIdempotencyKey(String key);

}
