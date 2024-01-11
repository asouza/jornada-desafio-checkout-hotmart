package com.deveficiente.desafiocheckouthotmart.configuracoes;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;

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
    public void novaConfiguracao(@Valid @RequestBody NovaConfiguracaoRequest request) throws BindException {
        Configuracao novaConfiguracao = request.toModel();
        
        if(novaConfiguracao.isDefault() && configuracaoRepository.findByOpcaoDefaultIsTrue().isPresent()) {
        	
            throw BindExceptionFactory.createGlobalError("Já existe uma configuração default registrada");
        }        
        
		configuracaoRepository.save(novaConfiguracao);
    }
}
