package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;

@RestController
public class CalculaValoresAReceberPorCompra {

	private CompraRepository compraRepository;
	private EntityManager manager;
	private ExecutaTransacao executaTransacao;

	public CalculaValoresAReceberPorCompra(CompraRepository compraRepository,
			EntityManager manager,
			ExecutaTransacao executaTransacao) {
		super();
		this.compraRepository = compraRepository;
		this.manager = manager;
		this.executaTransacao = executaTransacao;
	}

	private static final Logger log = LoggerFactory
			.getLogger(CalculaValoresAReceberPorCompra.class);

	@GetMapping("/879r4hfkr89y4i4gkuhg34iuygg")
	public void calcula() {
		List<Compra> comprasFinalizadas = compraRepository
				.listaComprasNaoProvisionadas();

		// aqui poderia configurar algum batch
		/*
		 * também poderia usar a solução proposta por Rafael Ponte aqui
		 * https://www.youtube.com/watch?v=I_kEO_HPfBU&t=1399s
		 */
		for (Compra compra : comprasFinalizadas) {
			Provisionamento novoProvisionamento = compra
					.calculaProvisionamento();
			try {
				executaTransacao.semRetorno(() -> {
					manager.persist(novoProvisionamento);
					/*
					 * A compra tem uma versão para suportar que o
					 * provisionamento possa ser executado por multiplas
					 * instancias.
					 */
					compra.provisionouOPagamento();
				});
			} catch (OptimisticLockException e) {
				Log5WBuilder.metodo().oQueEstaAcontecendo(
						"Aconteceu um problema de provisionamento por conta de atualizacao concorrente")
						.adicionaInformacao("codigoCompra",
								compra.getCodigo().toString())
						.erro(log, e);
			}

			catch (Exception e) {
				Log5WBuilder.metodo().oQueEstaAcontecendo(
						"Aconteceu um problema de provisionamento desconhecido")
						.adicionaInformacao("codigoCompra",
								compra.getCodigo().toString())
						.erro(log, e);
			}
		}
	}
}
