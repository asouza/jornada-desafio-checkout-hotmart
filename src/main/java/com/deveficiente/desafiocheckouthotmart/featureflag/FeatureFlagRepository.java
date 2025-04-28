package com.deveficiente.desafiocheckouthotmart.featureflag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
