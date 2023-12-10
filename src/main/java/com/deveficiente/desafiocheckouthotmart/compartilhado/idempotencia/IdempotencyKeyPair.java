package com.deveficiente.desafiocheckouthotmart.compartilhado.idempotencia;

import com.deveficiente.desafiocheckouthotmart.compartilhado.JsonHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class IdempotencyKeyPair {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	@NotBlank
	private String idempotencyKey;
	@NotBlank
	private String idempotencyValue;
	@NotBlank
	private String klass;

	@Deprecated
	public IdempotencyKeyPair() {
	}
	
	/**
	 * 
	 * @param key
	 * @param body must be json serializable object
	 */
	public IdempotencyKeyPair(String key, Object body) {
		this.idempotencyKey = key;
		this.idempotencyValue = JsonHelper.json(body);
		this.klass = body.getClass().getName();
	}
	
	public String getIdempotencyValue() {
		return idempotencyValue;
	}
	
	public Object desserialize() {
		try {
			Class<?> originalClass = this.getClass().getClassLoader().loadClass(this.klass);
			return JsonHelper.desserializa(this.idempotencyValue, originalClass);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}		
	}

}
