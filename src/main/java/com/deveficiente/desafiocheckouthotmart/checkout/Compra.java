package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Conta conta;
	@ManyToOne
	private Oferta oferta;
	@OneToOne(mappedBy = "compra", cascade = CascadeType.PERSIST)
	private MetadadosCompra metadados;
	@OneToMany(mappedBy = "compra", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private List<TransacaoCompra> transacoes = new ArrayList<>();
	private UUID codigo = UUID.randomUUID();

	@Deprecated
	public Compra() {
		// TODO Auto-generated constructor stub
	}

	public Compra(Conta conta, Oferta oferta,
			Function<Compra, MetadadosCompra> funcaoCriadoraMetadados) {
		this.conta = conta;
		this.oferta = oferta;
		this.transacoes.add(new TransacaoCompra(this, StatusCompra.iniciada));
		this.metadados = funcaoCriadoraMetadados.apply(this);
	}

	public void finaliza(String idTransacao) {
		Assert.state(!temTransacaoComStatus(StatusCompra.finalizada),
				"Uma compra finalizada não deveria ser finalizada novamente =>"
						+ this.codigo);

		this.transacoes.add(new TransacaoCompra(this, StatusCompra.finalizada,
				idTransacao));
	}
	
	public BigDecimal getPreco() {
		return oferta.getPreco();
	}

	private boolean temTransacaoComStatus(StatusCompra status) {
		return buscaTransacaoComStatus(status).isPresent();
	}

	private Optional<TransacaoCompra> buscaTransacaoComStatus(
			StatusCompra status) {
		return this.transacoes.stream()
				.filter(transacao -> transacao.statusIgual(status)).findFirst();
	}

	public UUID getCodigo() {
		return codigo;
	}

	public Oferta getOferta() {
		return oferta;
	}

	public MetadadosCompra getMetadados() {
		return metadados;
	}

	public UUID getCodigoConta() {
		return this.conta.getCodigo();
	}

	public Conta getConta() {
		return this.conta;
	}

	public Optional<String> buscaIdTransacao() {
		Optional<TransacaoCompra> possivelTransacao = buscaTransacaoComStatus(
				StatusCompra.finalizada);
		return possivelTransacao.flatMap(tx -> tx.buscaIdTransacao());

	}

	public TransacaoCompra getUltimaTransacaoRegistrada() {
		assertTemTransacaoIniciada();
		
		return this.transacoes.get(this.transacoes.size()-1);
	}

	private void assertTemTransacaoIniciada() {
		Assert.state(temTransacaoComStatus(StatusCompra.iniciada), "Toda compra deveria nascer com uma transacao indicando que ela foi iniciada => "+this.codigo);
	}

	public void adicionaTransacao(StatusCompra status) {
		assertTemTransacaoIniciada();	
		this.transacoes.add(new TransacaoCompra(this, status));
	}

	public boolean pertenceConta(Conta outraConta) {
		return this.conta.equals(outraConta);
	}

}
