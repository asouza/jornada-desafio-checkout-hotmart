package com.deveficiente.desafiocheckouthotmart.ofertas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Entity
public class Oferta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull
	@ManyToOne
	private Produto produto;
	private @NotNull @Positive BigDecimal preco;
	private @NotNull @Min(1) @Max(12) int numeroMaximoParcelas;
	private @NotNull QuemPagaJuros quemPagaJuros;
	private @NotBlank String nome;
	@NotNull
	@PastOrPresent
	private LocalDateTime instanteCriacao = LocalDateTime.now();
	@NotNull
	private UUID codigo = UUID.randomUUID();

	// TODO refactor aqui será que não é melhor ter uma lista trackear?
	private boolean ativa = true;
	private boolean principal = false;
	@ElementCollection
	private List<ValorParcelaMes> valoresParcelas;

	@Deprecated
	public Oferta() {
		// TODO Auto-generated constructor stub
	}

	public Oferta(Produto produto, @NotBlank String nome,
			@NotNull @Positive BigDecimal preco,
			@NotNull @Min(1) @Max(12) Integer numeroMaximoParcelas,
			@NotNull QuemPagaJuros quemPagaJuros) {
		this.produto = produto;
		this.nome = nome;
		this.preco = preco;
		this.numeroMaximoParcelas = numeroMaximoParcelas;
		this.quemPagaJuros = quemPagaJuros;
		/*
		 * #paraBlogar se o calculo for feito, vai precisar do if.. Se delegar
		 * para a enum, não precisa.
		 */

		this.valoresParcelas = this.quemPagaJuros.calculaParcelasParaCliente(this.preco, this.produto.getConfiguracao().getTaxaJuros(), this.numeroMaximoParcelas);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
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
		Oferta other = (Oferta) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		return true;
	}

	public String getNome() {
		return nome;
	}

	public boolean isPrincipal() {
		return this.principal;
	}

	public void defineComoPrincipal() {
		this.principal = true;
	}

	public boolean temMesmoCodigo(UUID codigo) {
		return this.codigo.equals(codigo);
	}

	public BigDecimal getPreco() {
		return this.preco;
	}

	public Produto getProduto() {
		return this.produto;
	}

	public List<ValorParcelaMes> getParcelaMes() {
		return this.valoresParcelas;
	}

}
