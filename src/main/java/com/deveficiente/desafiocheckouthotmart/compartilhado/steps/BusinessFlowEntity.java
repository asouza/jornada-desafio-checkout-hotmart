package com.deveficiente.desafiocheckouthotmart.compartilhado.steps;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class BusinessFlowEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private @NotBlank String flowName;
	@Column(unique = true)
	//TODO tem que criar uma chave unica unindo flowName + uniqueFlowCode
	private @NotBlank String uniqueFlowCode;
	@OneToMany(mappedBy = "businessFlowEntity",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private Set<BusinessFlowStep> steps = new HashSet<>();

	@Deprecated
	public BusinessFlowEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public BusinessFlowEntity(@NotBlank String flowName,
			@NotBlank String uniqueFlowCode) {
				this.flowName = flowName;
				this.uniqueFlowCode = uniqueFlowCode;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((uniqueFlowCode == null) ? 0 : uniqueFlowCode.hashCode());
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
		BusinessFlowEntity other = (BusinessFlowEntity) obj;
		if (uniqueFlowCode == null) {
			if (other.uniqueFlowCode != null)
				return false;
		} else if (!uniqueFlowCode.equals(other.uniqueFlowCode))
			return false;
		return true;
	}



	/**
	 * 
	 * @param stepName
	 * @param executionResult
	 * @return true for successful registration.  
	 */
	public BusinessFlowStep registerStep(String stepName,Object executionResult) {
		BusinessFlowStep newStep = new BusinessFlowStep(this,stepName,executionResult);						
		if(this.steps.add(newStep)) {
			return newStep;
		}
		
		//exist the Step for sure
		
		return this.steps.stream()
				.filter(step -> step.equals(newStep))
				.findFirst()
				.get();
	}
	
	public String getUniqueFlowCode() {
		return uniqueFlowCode;
	}


	public Long getId() {
		return id;
	}

}
