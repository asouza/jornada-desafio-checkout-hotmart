package com.deveficiente.desafiocheckouthotmart.compartilhado.steps;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.util.Assert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
public class BusinessFlowStep {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private BusinessFlowEntity businessFlowEntity;
	@NotBlank
	private String stepName;
	@PastOrPresent
	@NotNull
	private LocalDateTime createdAt = LocalDateTime.now();
	@PastOrPresent	
	@Version
	private LocalDateTime finishedAt;
	private String executionResult;
	
	@Deprecated
	public BusinessFlowStep() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * aqui precisaria adicionar uma chave composta na tabela
	 * para garantir a integridade de stepName e businessFlow
	 */

	public BusinessFlowStep(BusinessFlowEntity businessFlowEntity,
			String stepName) {
				this.businessFlowEntity = businessFlowEntity;
				this.stepName = stepName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((businessFlowEntity == null) ? 0
				: businessFlowEntity.hashCode());
		result = prime * result
				+ ((stepName == null) ? 0 : stepName.hashCode());
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
		BusinessFlowStep other = (BusinessFlowStep) obj;
		if (businessFlowEntity == null) {
			if (other.businessFlowEntity != null)
				return false;
		} else if (!businessFlowEntity.equals(other.businessFlowEntity))
			return false;
		if (stepName == null) {
			if (other.stepName != null)
				return false;
		} else if (!stepName.equals(other.stepName))
			return false;
		return true;
	}

	
	/**
	 * 
	 * @param executionResult
	 * @return toString of result
	 */
	public String finish(Object executionResult) {
		Assert.state(Objects.isNull(this.finishedAt), "Step does only accept one finish");
		
		this.finishedAt = LocalDateTime.now();
		this.executionResult = executionResult.toString();
		
		return this.executionResult;
	}

	public String getExecutionResult() {
		Assert.notNull(this.finishedAt, "You should not invoke this method while the step is not finished");
		
		return this.executionResult;
	}
	
	

}
