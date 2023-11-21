package com.deveficiente.desafiocheckouthotmart.produtos;

import java.util.UUID;

import com.deveficiente.desafiocheckouthotmart.contas.Conta;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private @NotBlank String nome;
	private @NotBlank String descricao;
	@NotNull
	@ManyToOne
	private Conta conta;
	@NotNull
	private UUID codigo;
	
	@Deprecated
	public Produto() {
		// TODO Auto-generated constructor stub
	}

	public Produto(@NotBlank String nome, @NotBlank String descricao,
			@NotNull Conta conta) {
		this.nome = nome;
		this.descricao = descricao;
		this.conta = conta;
		this.codigo = UUID.randomUUID();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conta == null) ? 0 : conta.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Produto other = (Produto) obj;
		if (conta == null) {
			if (other.conta != null)
				return false;
		} else if (!conta.equals(other.conta))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	public String getNome() {
		return this.nome;
	}

	@Override
	public String toString() {
		return "Produto [nome=" + nome + "]";
	}
	
	

}
