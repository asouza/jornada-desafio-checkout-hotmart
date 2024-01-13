package com.deveficiente.desafiocheckouthotmart.checkout;

import java.time.LocalDate;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Entity
public class Provisionamento {


	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	@NotNull
	private Compra compra;
	@Embedded
	@NotNull
	@Valid
	private ValoresParaTodosEnvolvidos valores;
	@NotNull
	@FutureOrPresent
	private LocalDate dataLiberacaoPagamento;
	
	@Deprecated
	public Provisionamento() {
		// TODO Auto-generated constructor stub
	}
	
	public Provisionamento(Compra compra, ValoresParaTodosEnvolvidos valores,
			LocalDate dataLiberacaoPagamento) {
				this.compra = compra;
				this.valores = valores;
				this.dataLiberacaoPagamento = dataLiberacaoPagamento;
		// TODO Auto-generated constructor stub
	}
	
	

}
