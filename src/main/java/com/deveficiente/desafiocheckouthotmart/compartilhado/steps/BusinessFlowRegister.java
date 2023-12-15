package com.deveficiente.desafiocheckouthotmart.compartilhado.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Log5WBuilder;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;

@Component
public class BusinessFlowRegister {

	private BusinessFlowRepository businessFlowRepository;
	private TransactionTemplate transactionTemplate;

	private static final Logger log = LoggerFactory
			.getLogger(BusinessFlowRegister.class);

	public BusinessFlowRegister(BusinessFlowRepository businessFlowRepository,
			TransactionTemplate transactionTemplate) {
		super();
		this.businessFlowRepository = businessFlowRepository;
		this.transactionTemplate = transactionTemplate;
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

		return new BusinessFlowSteps(newFlow, transactionTemplate, businessFlowRepository);
	}

}