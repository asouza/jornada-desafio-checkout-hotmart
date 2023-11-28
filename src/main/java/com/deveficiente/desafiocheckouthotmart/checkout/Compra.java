package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.ArrayList;
import java.util.List;
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
		this.metadados = funcaoCriadoraMetadados.apply(this);
		this.transacoes.add(new TransacaoCompra(this,StatusCompra.iniciada));
	}

	public void finaliza(String idTransacao) {
		Assert.state(!temTransacaoComStatus(StatusCompra.finalizada), "Uma compra finalizada não deveria ser finalizada novamente =>"+this.codigo);
		
		this.transacoes
				.add(new TransacaoCompra(this,StatusCompra.finalizada, idTransacao));
	}

	private boolean temTransacaoComStatus(StatusCompra status) {
		return this.transacoes.stream()
				.filter(transacao -> transacao
						.statusIgual(status))
				.toList().iterator().hasNext();
	}

}
