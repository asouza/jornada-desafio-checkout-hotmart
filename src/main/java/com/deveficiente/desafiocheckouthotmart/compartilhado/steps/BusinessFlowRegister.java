package com.deveficiente.desafiocheckouthotmart.compartilhado.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;

/*
 * - O step precisa ter um controle fino de que começou a ser executado e pode ter parado no meio. 
 * - Da para ter step com expiração de execução
 * - Uma consequencia negativa é um monte de lazy load explodindo...
 */

@Component
public class BusinessFlowRegister {

	private BusinessFlowRepository businessFlowRepository;
	private TransactionTemplate transactionTemplate;
	private EntityManager manager;

	private static final Logger log = LoggerFactory
			.getLogger(BusinessFlowRegister.class);

	public BusinessFlowRegister(BusinessFlowRepository businessFlowRepository,
			TransactionTemplate transactionTemplate, EntityManager manager) {
		super();
		this.businessFlowRepository = businessFlowRepository;
		this.transactionTemplate = transactionTemplate;
		this.manager = manager;
	}

	/**
	 * 
	 * @param flowName
	 * @param uniqueFlowCode
	 * @return
	 */
	public BusinessFlowSteps execute(@NotBlank String flowName,
			@NotBlank String uniqueFlowCode) {

		/*
		 * Meu chute é que ficar executando os fluxos de negocios multiplas
		 * vezes é exceção e nÀo regra... Então tentar salvar e tratar exception
		 * não deve penalizar tanto
		 */
		BusinessFlowEntity newFlow;
		try {

			/*
			 * Aqui executa a transação no menor escopo possível pq se der um
			 * problema de integridade ele marca a transação como
			 * problemática... E aí lasca o resto.
			 * 
			 * Então, se der problema, a próxima lógica precisa acontecer em
			 * outro contexto transacional.
			 */
			newFlow = transactionTemplate.execute(status -> {
				return businessFlowRepository
						.save(new BusinessFlowEntity(flowName, uniqueFlowCode));
			});
		} catch (DataIntegrityViolationException e) {

			// TODO deveria ser debug
			Log5WBuilder.metodo()
					.oQueEstaAcontecendo(
							"Loading already registered business flow")
					.adicionaInformacao("uniqueFlowCode", uniqueFlowCode)
					.info(log);

			newFlow = businessFlowRepository
					.getByUniqueFlowCode(uniqueFlowCode);
		}

		return new BusinessFlowSteps(newFlow, transactionTemplate,
				businessFlowRepository,manager);
	}

}
