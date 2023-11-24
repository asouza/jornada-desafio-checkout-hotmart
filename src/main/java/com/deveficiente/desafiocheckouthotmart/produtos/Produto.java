package com.deveficiente.desafiocheckouthotmart.produtos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.JaExisteProdutoComMesmoNomeException;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@OneToMany(mappedBy = "produto",cascade = CascadeType.PERSIST)
	private Set<Oferta> ofertas = new HashSet<>();

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

	//TODO tests
	public Result<RuntimeException, Oferta> adicionaOferta(
			Function<Produto, Oferta> funcaoCriadoraOferta) {
		
		Oferta novaOferta = funcaoCriadoraOferta.apply(this);
		boolean adicionou = this.ofertas.add(novaOferta);
		
        if(adicionou) {
        	return Result.successWithReturn(novaOferta);
        }
        return Result.failWithProblem(new JaExisteOfertaComMesmoNomeException(novaOferta));				
	}

	/**
	 * 
	 * @param ofertaAlvo
	 * @return se conseguiu definir como principal
	 */
	//TODO tests
	public boolean tentaDefinirOfertaComoPrincipal(Oferta ofertaAlvo) {
		boolean naoExistePrincipal = this.ofertas.stream().noneMatch(Oferta :: isPrincipal);
		
		if(naoExistePrincipal) {
			//deve ter uma oferta só
			List<Oferta> ofertasEncontradas = this
				.ofertas
				.stream()
				.filter(oferta -> oferta.equals(ofertaAlvo))
				.collect(Collectors.toList());
			
			Assert.isTrue(ofertasEncontradas.size() == 1, "Como não tem oferta principal ainda, a oferta parametro deveria existir na colecao de ofertas do produto");
			ofertasEncontradas.get(0).defineComoPrincipal();
		}
		
		return naoExistePrincipal;
	}

	public UUID getCodigo() {
		return codigo;
	}

}
