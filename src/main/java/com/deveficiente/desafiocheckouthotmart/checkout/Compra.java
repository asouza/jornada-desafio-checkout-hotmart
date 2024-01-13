package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.cupom.Cupom;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@ICP(13)
/*
 * A compra passa em 3 pontos do limimte de complexidade. Entretanto,
 * majoritariamente, este limite é ultrapassado por conta dos atributos
 * de controle. Por enquanto vou deixar como está.
 */
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@ICP
	private Conta conta;
	@ManyToOne
	@ICP
	private Oferta oferta;
	@OneToOne(mappedBy = "compra", cascade = CascadeType.PERSIST)
	@ICP
	private MetadadosCompra metadados;
	@OneToMany(mappedBy = "compra", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@ICP
	private List<TransacaoCompra> transacoes = new ArrayList<>();
	private UUID codigo = UUID.randomUUID();
	@ManyToOne
	@ICP
	private Cupom cupom;
	@Version
	private LocalDateTime instanteAtualizacao;
	@PastOrPresent
	private LocalDateTime instanteProvisionamento;
	@NotNull
	private BigDecimal precoMomento;
	@NotNull
	private UUID codigoOferta;
	@NotNull
	private UUID codigoProduto;
	@NotNull
	private BigDecimal precoFinal;
	@NotNull
	@ICP
	private QuemPagaJuros quemPagaJuros;
	@OneToOne(mappedBy = "compra",cascade = CascadeType.PERSIST)
	@ICP
	private Provisionamento provisionamento;

	private static final Logger log = LoggerFactory.getLogger(Compra.class);

	@Deprecated
	public Compra() {
		// TODO Auto-generated constructor stub
	}

	public Compra(Conta conta, Oferta oferta,
			Function<Compra, MetadadosCompra> funcaoCriadoraMetadados) {
		this(conta,oferta,null,funcaoCriadoraMetadados);
	}

	public Compra(Conta conta, Oferta oferta, Cupom cupom,
			Function<Compra, MetadadosCompra> funcaoCriadoraMetadados) {
		this.conta = conta;
		this.oferta = oferta;
		this.precoMomento = oferta.getPreco();
		this.codigoOferta = oferta.getCodigo();
		this.codigoProduto = oferta.getProduto().getCodigo();
		this.transacoes.add(new TransacaoCompra(this, StatusCompra.iniciada));
		this.metadados = funcaoCriadoraMetadados.apply(this);		
		this.cupom = cupom;
		this.quemPagaJuros = oferta.getPagaJuros();
		/*
		 * O cupom aqui pode ser nulo porque este construtor é chamado
		 * a partir de outro. 
		 */
		this.precoFinal = 
				Optional
				.ofNullable(this.cupom)
				.map(cupomExistente -> cupomExistente.aplicaDesconto(this.precoMomento))
				.orElse(this.precoMomento);
		
		
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
		//@ICP
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
		//@ICP
		return possivelTransacao.flatMap(tx -> tx.buscaIdTransacao());

	}

	public TransacaoCompra getUltimaTransacaoRegistrada() {
		assertTemTransacaoIniciada();

		return this.transacoes.get(this.transacoes.size() - 1);
	}

	private void assertTemTransacaoIniciada() {
		Assert.state(temTransacaoComStatus(StatusCompra.iniciada),
				"Toda compra deveria nascer com uma transacao indicando que ela foi iniciada => "
						+ this.codigo);
	}

	public void adicionaTransacao(StatusCompra status) {
		assertTemTransacaoIniciada();
		this.transacoes.add(new TransacaoCompra(this, status));
	}

	/**
	 * Adiciona uma transacao apenas se não tiver uma com o status em questão.
	 * 
	 * @param status
	 * @return se adicionou
	 */
	public boolean adicionaTransacaoCondicional(StatusCompra status) {
		assertTemTransacaoIniciada();

		//@ICP
		if (temTransacaoComStatus(status)) {
			// TODO aqui devia ser debug
			Log5WBuilder.metodo().oQueEstaAcontecendo("Não adicionou transacao")
					.adicionaInformacao("status", status.toString())
					.adicionaInformacao("codigoCompra", this.codigo.toString())
					.info(log);

			return false;
		}

		this.transacoes.add(new TransacaoCompra(this, status));

		return true;
	}

	public boolean pertenceConta(Conta outraConta) {
		return this.conta.equals(outraConta);
	}

	public Long getId() {
		return id;
	}

	public Provisionamento calculaProvisionamento() {

		// começo super restritivo. Se tiver argumento, fica mais soft.
		Assert.state(temTransacaoComStatus(StatusCompra.finalizada),
				"Provisionamento só pode ser calculado para compra finalizada");
		
		//deixando a pre condicao mais soft... Aqui abre uma porta maior para bug, na minha opinião.
		//@ICP
		if(this.provisionamento != null) {
			//devia ser debug, já que não tem a ver com o que está definido para info
			Log5WBuilder
				.metodo()
				.oQueEstaAcontecendo("Calculo de provisionamento para provisionamento já feito")
				.adicionaInformacao("idCompra", this.id.toString())
				.adicionaInformacao("codigoCompra", this.codigo.toString())
				.info(log);
			
			return this.provisionamento;
		}
		

		// quase que eu implemento de novo, mesmo tendo um metodo privado
		// neste ponto a gente sabe que tem uma transacao finalizada
		TransacaoCompra transacaoFinalizacao = buscaTransacaoComStatus(
				StatusCompra.finalizada).get();

		//@ICP
		Configuracao configuracao = this.conta.getConfiguracao();
		
		LocalDate dataLiberacaoPagamento = configuracao
				.calculaDiaPagamento(transacaoFinalizacao);
		 
		
		BigDecimal valorComissao = configuracao.calculaComissao(this.precoFinal);
		BigDecimal valorDeRepasse = this.precoFinal.subtract(valorComissao);		
		BigDecimal descontoRepassePorTaxasExtras = this.metadados.calculaPossivelDescontoRepasse(quemPagaJuros, valorDeRepasse, configuracao);
		
		//@ICP
		ValoresParaTodosEnvolvidos valores = new ValoresParaTodosEnvolvidos(valorComissao,valorDeRepasse,descontoRepassePorTaxasExtras);  
				
		this.provisionamento = new Provisionamento(this,valores,
				dataLiberacaoPagamento);
		
		return this.provisionamento;
	}

	public void provisionouOPagamento() {
		// aqui poderia só ignorar a chamada e logar. Outra opcao...
		Assert.state(Objects.isNull(this.instanteProvisionamento),
				"Compra já foi provisionada");
		
		Assert.state(Objects.nonNull(this.provisionamento),
				"O provisionamento precisa ser calculado antes. Já chamou o calculaProvisionamento?");
		
		this.instanteProvisionamento = LocalDateTime.now();
	}

}
