package com.deveficiente.desafiocheckouthotmart.configuracoes;

import org.mockito.ArgumentMatcher;
import org.springframework.test.util.ReflectionTestUtils;

public class ConfiguracaoMatcher implements ArgumentMatcher<Configuracao> {

    private Configuracao expected;

    public ConfiguracaoMatcher(Configuracao expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Configuracao configuracao) {
        return get(configuracao,"taxaComissao").equals(get(expected,"taxaComissao"))
                && get(configuracao,"taxaJuros").equals(get(configuracao,"taxaJuros"))
                && get(configuracao,"opcaoDefault").equals(get(configuracao,"opcaoDefault"));
    }

    private Object get(Object reference, String fieldName) {
        return ReflectionTestUtils.getField(reference, fieldName);
    }
}
