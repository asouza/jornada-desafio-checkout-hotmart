package com.deveficiente.desafiocheckouthotmart.configuracoes;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.deveficiente.desafiocheckouthotmart.JSONFromMap;

//#copilotGerou : Versão inicial com alguns problemas, por exmeplo parametros errados, mas gerou bem.
public class NovaConfiguracaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConfiguracaoRepository configuracaoRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        NovaConfiguracaoController controller = new NovaConfiguracaoController(configuracaoRepository);
        // #copilotGerou : Achei legal isso aqui, preciso aprender mais inclusive. 
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void deveSalvarNovaConfiguracao() throws Exception {
        Map<String,String> request = Map.of("taxaComissao","10","taxaJuros","1","opcaoDefault","true");
        mockMvc.perform(MockMvcRequestBuilders.post("/configuracoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONFromMap.execute(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(configuracaoRepository, times(1)).save(Mockito.argThat(new ConfiguracaoMatcher(new Configuracao(BigDecimal.TEN, BigDecimal.ONE, true))));
    }

    @Test
    public void naoDeveSalvarNovaConfiguracaoSeJaExisteDefault() throws Exception {
        Configuracao configuracaoExistente = new Configuracao(BigDecimal.TEN, BigDecimal.ONE, true);
        when(configuracaoRepository.findByOpcaoDefaultIsTrue()).thenReturn(Optional.of(configuracaoExistente));

        Map<String,String> request = Map.of("taxaComissao","10","taxaJuros","1","opcaoDefault","true");
        mockMvc.perform(MockMvcRequestBuilders.post("/configuracoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONFromMap.execute(request)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        verify(configuracaoRepository, never()).save(Mockito.argThat(new ConfiguracaoMatcher(new Configuracao(BigDecimal.TEN, BigDecimal.ONE, true))));
    }
}