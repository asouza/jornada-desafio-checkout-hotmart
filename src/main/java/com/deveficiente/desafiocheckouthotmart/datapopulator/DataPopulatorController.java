package com.deveficiente.desafiocheckouthotmart.datapopulator;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.ConfiguracaoRepository;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.ContaRepository;
import com.deveficiente.desafiocheckouthotmart.featureflag.FeatureFlag;
import com.deveficiente.desafiocheckouthotmart.featureflag.FeatureFlagRepository;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.deveficiente.desafiocheckouthotmart.produtos.ProdutoRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Controller responsável por popular o banco com dados para testes de checkout
 */
@RestController
@RequestMapping("/data-populator")
public class DataPopulatorController {

    private final ConfiguracaoRepository configuracaoRepository;
    private final ContaRepository contaRepository;
    private final ProdutoRepository produtoRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final Random random = new Random();

    public DataPopulatorController(
            ConfiguracaoRepository configuracaoRepository,
            ContaRepository contaRepository,
            ProdutoRepository produtoRepository,
            FeatureFlagRepository featureFlagRepository) {
        this.configuracaoRepository = configuracaoRepository;
        this.contaRepository = contaRepository;
        this.produtoRepository = produtoRepository;
        this.featureFlagRepository = featureFlagRepository;
    }

    /**
     * Popula o banco de dados com:
     * - Uma configuração default
     * - 10.000 contas associadas à configuração default
     * - 2 produtos para cada conta
     * - 2 ofertas por produto, uma com juros pago pelo cliente e outra pelo vendedor
     * - Feature flags necessárias para o sistema (gateway1, gateway2, gateway3 e servico-email)
     */
    @PostMapping("/setup-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> setupApplicationData() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Cria configuração default (se não existir)
            Configuracao configuracao = getOrCreateDefaultConfiguration();
            response.put("configurationCreated", true);
            
            // Cria as feature flags necessárias
            createFeatureFlags();
            response.put("featureFlagsCreated", true);
            
            // Cria 10.000 contas
            int totalContas = 10000;
            int contasProcessadas = 0;
            
            for (int i = 1; i <= totalContas; i++) {
                // Cria conta
                String email = "user" + i + "@example.com";
                Conta conta = new Conta(email, configuracao);
                contaRepository.save(conta);
                
                // Cria 2 produtos para cada conta
                createProductsForAccount(conta);
                
                contasProcessadas++;
                
                // Log de progresso a cada 1000 contas
                if (i % 1000 == 0) {
                    System.out.println("Processadas " + i + " de " + totalContas + " contas");
                }
            }
            
            response.put("accountsCreated", contasProcessadas);
            response.put("productsCreated", contasProcessadas * 2);
            response.put("offersCreated", contasProcessadas * 4); // 2 ofertas por produto
            response.put("timeElapsedMs", System.currentTimeMillis() - startTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Cria feature flags necessárias: gateway1, gateway2, gateway3 e servico-email
     * Todas são habilitadas por padrão
     */
    private void createFeatureFlags() {
        createFeatureFlag("gateway1", true);
        createFeatureFlag("gateway2", true);
        createFeatureFlag("gateway3", true);
        createFeatureFlag("servico-email", true);
        
        System.out.println("Feature flags criadas com sucesso: gateway1, gateway2, gateway3, servico-email!");
    }
    
    /**
     * Cria uma feature flag caso não exista
     */
    private FeatureFlag createFeatureFlag(String codigo, boolean habilitada) {
        return featureFlagRepository.findByCodigo(codigo)
                .orElseGet(() -> {
                    FeatureFlag novaFeatureFlag = new FeatureFlag(codigo, habilitada);
                    return featureFlagRepository.save(novaFeatureFlag);
                });
    }

    /**
     * Cria uma configuração default ou retorna a existente
     */
    private Configuracao getOrCreateDefaultConfiguration() {
        return configuracaoRepository.findByOpcaoDefaultIsTrue()
                .orElseGet(() -> {
                    BigDecimal taxaComissao = new BigDecimal("10.0"); // 10%
                    BigDecimal taxaJuros = new BigDecimal("2.5"); // 2.5% ao mês
                    Boolean opcaoDefault = true;
                    
                    Configuracao novaConfig = new Configuracao(taxaComissao, taxaJuros, opcaoDefault);
                    return configuracaoRepository.save(novaConfig);
                });
    }

    /**
     * Cria dois produtos para a conta especificada
     */
    private void createProductsForAccount(Conta conta) {
        // Produto 1 - Curso
        Produto produto1 = new Produto(
                "Curso " + randomNameSuffix(), 
                "Descrição do curso " + randomNameSuffix(), 
                conta);
        
        Result<RuntimeException, Produto> resultadoProduto1 = conta.adicionaProduto(c -> produto1);
        if (resultadoProduto1.isSuccess()) {
            produtoRepository.save(produto1);
            createOffersForProduct(produto1);
        }
        
        // Produto 2 - E-book
        Produto produto2 = new Produto(
                "E-book " + randomNameSuffix(), 
                "Descrição do e-book " + randomNameSuffix(), 
                conta);
        
        Result<RuntimeException, Produto> resultadoProduto2 = conta.adicionaProduto(c -> produto2);
        if (resultadoProduto2.isSuccess()) {
            produtoRepository.save(produto2);
            createOffersForProduct(produto2);
        }
    }

    /**
     * Cria duas ofertas para o produto especificado:
     * - Uma com juros pagos pelo cliente
     * - Uma com juros pagos pelo vendedor
     */
    private void createOffersForProduct(Produto produto) {
        // Oferta 1 - Cliente paga juros
        BigDecimal preco1 = new BigDecimal(50 + random.nextInt(200));
        int maxParcelas1 = 6 + random.nextInt(7); // Entre 6 e 12 parcelas
        
        Result<RuntimeException, Oferta> resultadoOferta1 = produto.adicionaOferta(p -> 
            new Oferta(p, "Oferta padrão - Cliente paga juros", preco1, maxParcelas1, QuemPagaJuros.cliente)
        );
        
        if (resultadoOferta1.isSuccess()) {
            Oferta oferta1 = resultadoOferta1.getSuccessReturn();
            oferta1.defineComoPrincipal();
            // Oferta já foi persistida via cascade
        }
        
        // Oferta 2 - Vendedor paga juros
        BigDecimal preco2 = preco1.multiply(new BigDecimal("1.1")); // 10% mais cara
        int maxParcelas2 = maxParcelas1;
        
        Result<RuntimeException, Oferta> resultadoOferta2 = produto.adicionaOferta(p -> 
            new Oferta(p, "Oferta especial - Vendedor paga juros", preco2, maxParcelas2, QuemPagaJuros.vendedor)
        );
        
        // Oferta já foi persistida via cascade
    }
    
    /**
     * Gera um sufixo aleatório para os nomes
     */
    private String randomNameSuffix() {
        String[] adjectives = {"Avançado", "Profissional", "Básico", "Premium", "Exclusivo", "Essencial", "Master", "Express"};
        String[] topics = {"Java", "Spring", "Web", "Cloud", "DevOps", "Microservices", "API", "Full Stack", "React", "Angular"};
        
        return adjectives[random.nextInt(adjectives.length)] + " de " + topics[random.nextInt(topics.length)];
    }
}