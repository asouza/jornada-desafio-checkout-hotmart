package com.deveficiente.desafiocheckouthotmart.contas;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.ConfiguracaoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
public class NovaContaController {
    
    private ContaRepository contaRepository;
    private ConfiguracaoRepository configuracaoRepository;

    public NovaContaController(ContaRepository contaRepository, ConfiguracaoRepository configuracaoRepository) {
        this.contaRepository = contaRepository;
        this.configuracaoRepository = configuracaoRepository;
    }

    @PostMapping("/contas")
    @Transactional
    public void cria(@RequestBody @Valid NovaContaRequest request) {
        Configuracao configuracaoDefault = configuracaoRepository.findByOpcaoDefaultIsTrue()
            .orElseThrow(() -> new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível encontrar uma configuração padrão"
            ));

        Conta conta = request.toModel(configuracaoDefault);
        contaRepository.save(conta);
    }
}