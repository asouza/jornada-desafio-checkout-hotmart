package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.Nullable;

@Component
public class IdempotencyValueFinder {

	private IdempotencyKeyPairRepository idempotencyKeyPairRepository;

	public IdempotencyValueFinder(
			IdempotencyKeyPairRepository idempotencyKeyPairRepository) {
		super();
		this.idempotencyKeyPairRepository = idempotencyKeyPairRepository;
	}

	/**
	 * 
	 * @param key idempotency key. Accept null ir order to make your life easier
	 * @return the possible stored object. Null if key is empty or null.
	 */
	public Optional<Object> execute(@Nullable String key) {

		if (!StringUtils.hasText(key)) {
			return Optional.empty();
		}

		Optional<IdempotencyKeyPair> possiblePair = idempotencyKeyPairRepository
				.findByIdempotencyKey(key);

		return possiblePair.map(pair -> {
			return pair.desserialize();
		});

	}
}
