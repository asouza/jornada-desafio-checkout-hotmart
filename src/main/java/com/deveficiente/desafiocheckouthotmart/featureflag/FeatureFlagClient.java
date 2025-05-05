package com.deveficiente.desafiocheckouthotmart.featureflag;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "featureFlagClient", url = "${app.feature-flag.url:${server.servlet.context-path:}/api}")
public interface FeatureFlagClient {
    
    @GetMapping("/feature-flags/{codigo}/status")
    ResponseEntity<FeatureFlagStatusResponse> verificarStatus(@PathVariable("codigo") String codigo);
}