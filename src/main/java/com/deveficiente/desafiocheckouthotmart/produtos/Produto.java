package com.deveficiente.desafiocheckouthotmart.produtos;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.cupom.Cupom;
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
	@OneToMany(mappedBy = "produto", cascade = CascadeType.PERSIST)
	private Set<Oferta> ofertas = new HashSet<>();
	@OneToMany(mappedBy = "produto", cascade = CascadeType.PERSIST)
	private Set<Cupom> cupons = new HashSet<>();

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

	// TODO tests
	public Result<RuntimeException, Oferta> adicionaOferta(
			Function<Produto, Oferta> funcaoCriadoraOferta) {

		Oferta novaOferta = funcaoCriadoraOferta.apply(this);
		boolean adicionou = this.ofertas.add(novaOferta);

		if (adicionou) {
			return Result.successWithReturn(novaOferta);
		}
		return Result.failWithProblem(
				new JaExisteOfertaComMesmoNomeException(novaOferta));
	}

	/**
	 * 
	 * @param ofertaAlvo
	 * @return se conseguiu definir como principal
	 */
	// TODO tests
	public boolean tentaDefinirOfertaComoPrincipal(Oferta ofertaAlvo) {
		boolean naoExistePrincipal = this.ofertas.stream()
				.noneMatch(Oferta::isPrincipal);

		if (naoExistePrincipal) {
			// deve ter uma oferta só
			List<Oferta> ofertasEncontradas = this.ofertas.stream()
					.filter(oferta -> oferta.equals(ofertaAlvo))
					.collect(Collectors.toList());

			Assert.isTrue(ofertasEncontradas.size() == 1,
					"Como não tem oferta principal ainda, a oferta parametro deveria existir na colecao de ofertas do produto");
			ofertasEncontradas.get(0).defineComoPrincipal();
		}

		return naoExistePrincipal;
	}

	public UUID getCodigo() {
		return codigo;
	}

	/**
	 * 
	 * @param codigo
	 * @return Oferta ou os possíveis problemas:
	 * <ul>
	 * 	<li>{@link OfertaInexistenteException}</li>
	 * </ul> 
	 *
	 * 
	 */
	public Optional<Oferta> buscaOferta(
			UUID codigo) {

		List<Oferta> ofertas = this.ofertas
				.stream()
				.filter(oferta -> oferta.temMesmoCodigo(codigo))
				.toList();
		
		Assert.isTrue(ofertas.size() <= 1, "Deveria haver no máximo uma oferta com o código "+codigo);
		
		//#hack aqui evita a gente fazer o if na mão. A gente já sabe que tem uma ou zero.
		return ofertas.stream().findFirst();
		
	}

	public Oferta getOfertaPrincipal() {
		List<Oferta> ofertas = this.ofertas
				.stream()
				.filter(oferta -> oferta.isPrincipal())
				.toList();
		
		Assert.isTrue(ofertas.size() == 1, "Deveria haver UMA oferta como principal para o produto "+this.codigo);
		
		return ofertas.get(0);
		
	}

	public Configuracao getConfiguracao() {
		//não era necessário delegar tanto, a parte ruim é que o de fora
		//ia conhecer muito o de dentro... Pode atrapalhar refatoracao etc.
		return this.conta.getConfiguracao();
	}

	/**
	 * 
	 * @param funcaoProdutoraCupom funcao que retorna um cupom para um produto
	 * @return true se tiver adicionado o cupom e false se já existia um igual
	 */
	public boolean adicionaCupom(Function<Produto, Cupom> funcaoProdutoraCupom) {
		Cupom novoCupom = funcaoProdutoraCupom.apply(this);
		return this.cupons .add(novoCupom);
	}

}
