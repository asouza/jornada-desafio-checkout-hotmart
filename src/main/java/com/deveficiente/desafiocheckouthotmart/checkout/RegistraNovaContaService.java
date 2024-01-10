package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.BuscasNecessariasParaPagamento;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;

import jakarta.persistence.EntityManager;

@ICP(3)
@Service
public class RegistraNovaContaService {

	@ICP
	private BuscasNecessariasParaPagamento buscasNecessariasParaPagamento;
	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(RegistraNovaContaService.class);
	
	public RegistraNovaContaService(
			BuscasNecessariasParaPagamento buscasNecessariasParaPagamento,
			EntityManager manager) {
		super();
		this.buscasNecessariasParaPagamento = buscasNecessariasParaPagamento;
		this.manager = manager;
	}

	/**
	 * 
	 * @param email
	 * @param funcaoProdutoraConta funcao que permite uma nova {@link Conta} ser criada, em caso de necessidade. 
	 * @return
	 */
	public Conta executa(String email) {
		Optional<Conta> possivelConta = buscasNecessariasParaPagamento
				.findContaByEmail(email);

		return possivelConta.orElseGet(() -> {
			Configuracao configuracaoDefault = buscasNecessariasParaPagamento
					.getConfiguracaoDefault();

			Assert.notNull(configuracaoDefault,
					"Deveria haver uma configuracao default criada");

			Conta novaConta = new Conta(email, configuracaoDefault);
			manager.persist(novaConta);

			Log5WBuilder
					// se pega o método automático aqui captura o lambda
					.metodo("PagaComCartaoCreditoController#executa")
					.oQueEstaAcontecendo(
							"Novo pagamento: salvando uma nova conta")
					.adicionaInformacao("codigoNovaConta",
							novaConta.getCodigo().toString())
					.info(log);

			return novaConta;
		});
	}
}
