package com.deveficiente.desafiocheckouthotmart.compartilhado.steps;

import java.util.Optional;

import org.springframework.transaction.support.TransactionTemplate;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;

public class BusinessFlowSteps {

	private BusinessFlowEntity currentFlow;
	private TransactionTemplate transactionTemplate;
	private BusinessFlowRepository businessFlowRepository;

	public BusinessFlowSteps(BusinessFlowEntity currentFlow,
			TransactionTemplate transactionTemplate,
			BusinessFlowRepository businessFlowRepository) {
		super();
		this.currentFlow = currentFlow;
		this.transactionTemplate = transactionTemplate;
		this.businessFlowRepository = businessFlowRepository;
	}

	public <T> String executeOnlyOnce(String stepName, Supplier<T> logic) {

		Optional<BusinessFlowStep> possibleStep = businessFlowRepository
				.findFinishedStepByName(stepName, currentFlow.getId());

		return

		possibleStep.map(step -> {
			return step.getExecutionResult();
		}).orElseGet(() -> {
			return transactionTemplate.execute(status -> {
				BusinessFlowStep step = currentFlow.registerStep(stepName);

				T executionResult = logic.get();

				return step.finish(executionResult);

			});
		});

	}

}
