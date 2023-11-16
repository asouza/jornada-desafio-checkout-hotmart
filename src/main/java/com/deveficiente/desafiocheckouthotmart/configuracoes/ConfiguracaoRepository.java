package com.deveficiente.desafiocheckouthotmart.configuracoes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {

    Optional<Configuracao> findByOpcaoDefaultIsTrue();

}
