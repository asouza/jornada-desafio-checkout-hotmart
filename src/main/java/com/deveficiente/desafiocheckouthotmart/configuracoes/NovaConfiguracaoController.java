package com.deveficiente.desafiocheckouthotmart.configuracoes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
public class NovaConfiguracaoController {
    
    private ConfiguracaoRepository configuracaoRepository;    

    

    public NovaConfiguracaoController(ConfiguracaoRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }



    @PostMapping("/configuracoes")
    @Transactional
    public void novaConfiguracao(@Valid @RequestBody NovaConfiguracaoRequest request) {
        if(configuracaoRepository.findByOpcaoDefaultIsTrue().isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Ja existe uma configuracao default");
        }

        configuracaoRepository.save(request.toModel());
    }
}
