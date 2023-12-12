package com.deveficiente.desafiocheckouthotmart.checkout;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
public class TransacaoCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	@PastOrPresent
	private LocalDateTime instante = LocalDateTime.now();
	@Enumerated(EnumType.STRING)
	@NotNull
	private StatusCompra status;
	private String idTransacao;
	@ManyToOne
	private Compra compra;
	
	@Deprecated
	public TransacaoCompra() {
		// TODO Auto-generated constructor stub
	}
	
	public TransacaoCompra(Compra compra,StatusCompra status) {
		this.compra = compra;
		this.status = status;
	}
	
	public TransacaoCompra(Compra compra, StatusCompra status,String idTransacao) {
		this.compra = compra;
		this.status = status;
		this.idTransacao = idTransacao;
	}

	public boolean statusIgual(StatusCompra statusBuscado) {
		return this.status.equals(statusBuscado);
	}
	
	public Optional<String> buscaIdTransacao() {
		return Optional.ofNullable(idTransacao);
	}

	public StatusCompra getStatus() {
		return this.status;
	}
	
	
	
}
