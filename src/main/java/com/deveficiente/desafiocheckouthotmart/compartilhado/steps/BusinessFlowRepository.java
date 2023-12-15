package com.deveficiente.desafiocheckouthotmart.compartilhado.steps;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.validation.constraints.NotBlank;

public interface BusinessFlowRepository extends JpaRepository<BusinessFlowEntity, Long>{

	BusinessFlowEntity getByUniqueFlowCode(@NotBlank String uniqueFlowCode);

	@Query("select bfs from BusinessFlowStep bfs where finishedAt is not null and stepName = :stepName and bfs.businessFlowEntity.id = :businessFlowId")
	Optional<BusinessFlowStep> findFinishedStepByName(@Param("stepName") String stepName, @Param("businessFlowId") Long businessFlowId);

}
