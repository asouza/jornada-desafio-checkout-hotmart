package com.deveficiente.desafiocheckouthotmart.featureflag;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/feature-flags")
public class FeatureFlagController {
    
    private final FeatureFlagRepository featureFlagRepository;
    
    public FeatureFlagController(FeatureFlagRepository featureFlagRepository) {
        this.featureFlagRepository = featureFlagRepository;
    }
    
    @PostMapping
    @Transactional
    public ResponseEntity<NovaFeatureFlagResponse> cadastrar(@RequestBody @Valid NovaFeatureFlagRequest request) {
        FeatureFlag featureFlag = request.toModel();
        featureFlagRepository.save(featureFlag);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NovaFeatureFlagResponse(featureFlag.getCodigo()));
    }
    
    @GetMapping("/{codigo}/status")
    public ResponseEntity<FeatureFlagStatusResponse> verificarStatus(@PathVariable String codigo) {
        FeatureFlag featureFlag = featureFlagRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature flag não encontrada: " + codigo));
        
        return ResponseEntity.ok(new FeatureFlagStatusResponse(featureFlag.isHabilitada()));
    }
    
    @PostMapping("/{codigo}/disable")
    @Transactional
    public ResponseEntity<FeatureFlagStatusResponse> disableFeatureFlag(@PathVariable String codigo) {
        FeatureFlag featureFlag = featureFlagRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature flag não encontrada: " + codigo));
        
        featureFlag.disable();
        featureFlagRepository.save(featureFlag);
        
        return ResponseEntity.ok(new FeatureFlagStatusResponse(featureFlag.isHabilitada()));
    }
    
    @PostMapping("/{codigo}/enable")
    @Transactional
    public ResponseEntity<FeatureFlagStatusResponse> enableFeatureFlag(@PathVariable String codigo) {
        FeatureFlag featureFlag = featureFlagRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature flag não encontrada: " + codigo));
        
        featureFlag.enable();
        featureFlagRepository.save(featureFlag);
        
        return ResponseEntity.ok(new FeatureFlagStatusResponse(featureFlag.isHabilitada()));
    }
}
