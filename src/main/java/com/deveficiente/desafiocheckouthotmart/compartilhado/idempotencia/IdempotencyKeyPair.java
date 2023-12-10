package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class IdempotencyKeyPair {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String idempotencyKey;
	private String idempotencyValue;

	@Deprecated
	public IdempotencyKeyPair() {
	}
	
	public IdempotencyKeyPair(String key, String value) {
		this.idempotencyKey = key;
		this.idempotencyValue = value;
	}
	
	public String getIdempotencyValue() {
		return idempotencyValue;
	}

}
