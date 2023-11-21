package com.deveficiente.desafiocheckouthotmart.contas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.JSONFromMap;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.ConfiguracaoRepository;

//#copilotGerou : Versão inicial com alguns problemas, por exemplo, imports faltando, mas gerou bem.
public class NovaContaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ConfiguracaoRepository configuracaoRepository;

    @BeforeEach
    public void setup() {
        //TODO: Descobrir se tem como usar o standaloneSetup com o @Valid com Validador usando injeção pelo Spring
        //tem que fazer o ValidatorFactory do Spring
        MockitoAnnotations.openMocks(this);
        NovaContaController controller = new NovaContaController(contaRepository, configuracaoRepository);
        // LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        // localValidatorFactoryBean.afterPropertiesSet();   

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            // .setValidator(localValidatorFactoryBean)
            .build();
    }

    @Test
    public void deveCriarNovaConta() throws Exception {
        Configuracao configuracaoDefault = new Configuracao(BigDecimal.TEN, BigDecimal.ONE, true);
        when(configuracaoRepository.findByOpcaoDefaultIsTrue()).thenReturn(Optional.of(configuracaoDefault));

        Map<String,String> request = Map.of("email","alberto@deveficiente.com");        

        mockMvc.perform(MockMvcRequestBuilders.post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONFromMap.execute(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(contaRepository, times(1)).save(new Conta("alberto@deveficiente.com",configuracaoDefault));
    }

    @Test
    public void naoDeveCriarContaComEmailDuplicado() throws Exception{
        Configuracao configuracaoDefault = new Configuracao(BigDecimal.TEN, BigDecimal.ONE, true);
        when(configuracaoRepository.findByOpcaoDefaultIsTrue()).thenReturn(Optional.of(configuracaoDefault));

        Map<String,String> request = Map.of("email","alberto@deveficiente.com");        

        mockMvc.perform(MockMvcRequestBuilders.post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONFromMap.execute(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(contaRepository, times(1)).save(new Conta("alberto@deveficiente.com",configuracaoDefault));

        //agora tenta cadastrar de novo

        mockMvc.perform(MockMvcRequestBuilders.post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONFromMap.execute(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());        
        
    }

    @Test
    public void naoDeveCriarNovaContaSeConfiguracaoDefaultNaoExistir() throws Exception {
        when(configuracaoRepository.findByOpcaoDefaultIsTrue()).thenReturn(Optional.empty());

        Map<String,String> request = Map.of("email","alberto@deveficiente.com");        

        mockMvc.perform(MockMvcRequestBuilders.post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONFromMap.execute(request)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));                

        verify(contaRepository, never()).save(Mockito.any());
    }
}