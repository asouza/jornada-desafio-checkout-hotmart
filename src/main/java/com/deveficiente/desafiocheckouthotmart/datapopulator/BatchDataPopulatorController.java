package com.deveficiente.desafiocheckouthotmart.datapopulator;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deveficiente.desafiocheckouthotmart.compartilhado.ExecutaTransacao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.ConfiguracaoRepository;
import com.deveficiente.desafiocheckouthotmart.ofertas.QuemPagaJuros;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Controller otimizado para inserção em massa de dados para checkout
 * Usa batch inserts e JDBC direto para melhor performance
 */
@RestController
@RequestMapping("/batch-data-populator")
public class BatchDataPopulatorController {

    private final ConfiguracaoRepository configuracaoRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ExecutaTransacao executaTransacao;
    private final Random random = new Random();
    
    @PersistenceContext
    private EntityManager entityManager;

    public BatchDataPopulatorController(
            ConfiguracaoRepository configuracaoRepository,
            JdbcTemplate jdbcTemplate,
            ExecutaTransacao executaTransacao) {
        this.configuracaoRepository = configuracaoRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.executaTransacao = executaTransacao;
    }

    /**
     * Popula o banco com dados para checkout usando inserções em batch
     * @param quantidade Número de contas a criar (default: 10000)
     */
    @PostMapping("/checkout-data")
    public ResponseEntity<Map<String, Object>> populateCheckoutDataInBatch(
            @RequestParam(defaultValue = "10000") int quantidade) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Cria configuração default (se não existir)
            Configuracao configuracao = getOrCreateDefaultConfiguration();
            response.put("configurationId", configuracao.getId());
            
            // Executar inserções em batches
            executaTransacao.comRetorno(() -> {
                insertBatchData(configuracao, quantidade);
                return true;
            });
            
            // Registra resultados
            long elapsedTime = System.currentTimeMillis() - startTime;
            response.put("accountsCreated", quantidade);
            response.put("productsCreated", quantidade * 2);
            response.put("offersCreated", quantidade * 4);
            response.put("timeElapsedMs", elapsedTime);
            response.put("recordsPerSecond", (quantidade * 7) / (elapsedTime / 1000.0)); // contas + produtos + ofertas
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(response);
        }
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
     * Insere dados em batch usando JDBC para melhor performance
     */
    private void insertBatchData(Configuracao configuracao, int quantidade) {
        final int BATCH_SIZE = 500;
        
        // Inserir contas
        for (int batch = 0; batch < quantidade; batch += BATCH_SIZE) {
            int batchEnd = Math.min(batch + BATCH_SIZE, quantidade);
            int batchSize = batchEnd - batch;
            
            List<Object[]> contasBatch = new ArrayList<>(batchSize);
            List<Long> contaIds = new ArrayList<>(batchSize);
            
            // Gerar IDs para as contas (assumindo identidade)
            for (int i = 0; i < batchSize; i++) {
                Long contaId = insertAndGetNextId("conta");
                contaIds.add(contaId);
            }
            
            // Preparar lote de contas
            for (int i = 0; i < batchSize; i++) {
                int index = batch + i;
                String email = "user" + index + "@example.com";
                UUID codigo = UUID.randomUUID();
                
                contasBatch.add(new Object[]{
                    contaIds.get(i),
                    codigo.toString(),
                    email,
                    configuracao.getId()
                });
            }
            
            // Inserir lote de contas
            jdbcTemplate.batchUpdate(
                "INSERT INTO conta (id, codigo, email, configuracao_id) VALUES (?, ?::uuid, ?, ?)",
                contasBatch
            );
            
            System.out.println("Inseridas " + batchEnd + " de " + quantidade + " contas");
            
            // Inserir produtos e ofertas para as contas deste lote
            insertProductsAndOffersForAccounts(contaIds);
            
            // Limpar memória a cada lote
            if (batch % 1000 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    
    /**
     * Insere produtos e ofertas para as contas fornecidas
     */
    private void insertProductsAndOffersForAccounts(List<Long> contaIds) {
        List<Object[]> produtosBatch = new ArrayList<>();
        List<Long> produtoIds = new ArrayList<>();
        
        // Gerar produtos para cada conta (2 produtos por conta)
        for (Long contaId : contaIds) {
            for (int i = 0; i < 2; i++) {
                Long produtoId = insertAndGetNextId("produto");
                produtoIds.add(produtoId);
                
                String tipo = (i == 0) ? "Curso" : "E-book";
                String nome = tipo + " " + randomNameSuffix();
                String descricao = "Descrição do " + tipo.toLowerCase() + " " + randomNameSuffix();
                UUID codigo = UUID.randomUUID();
                
                produtosBatch.add(new Object[]{
                    produtoId,
                    codigo.toString(),
                    descricao,
                    nome,
                    contaId
                });
            }
        }
        
        // Inserir produtos em batch
        jdbcTemplate.batchUpdate(
            "INSERT INTO produto (id, codigo, descricao, nome, conta_id) VALUES (?, ?::uuid, ?, ?, ?)",
            produtosBatch
        );
        
        // Inserir ofertas para os produtos (2 ofertas por produto)
        insertOffersForProducts(produtoIds);
    }
    
    /**
     * Insere ofertas para os produtos fornecidos
     */
    private void insertOffersForProducts(List<Long> produtoIds) {
        List<Object[]> ofertasBatch = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Long produtoId : produtoIds) {
            // Oferta 1 - Cliente paga juros
            Long oferta1Id = insertAndGetNextId("oferta");
            BigDecimal preco1 = new BigDecimal(50 + random.nextInt(200));
            int maxParcelas1 = 6 + random.nextInt(7); // Entre 6 e 12 parcelas
            UUID codigo1 = UUID.randomUUID();
            
            ofertasBatch.add(new Object[]{
                oferta1Id,
                codigo1.toString(),
                true, // ativa
                now,  // instanteCriacao
                "Oferta padrão - Cliente paga juros",
                maxParcelas1,
                true, // principal
                preco1,
                0,    // quemPagaJuros (0 = cliente)
                produtoId
            });
            
            // Oferta 2 - Vendedor paga juros
            Long oferta2Id = insertAndGetNextId("oferta");
            BigDecimal preco2 = preco1.multiply(new BigDecimal("1.1")); // 10% mais cara
            UUID codigo2 = UUID.randomUUID();
            
            ofertasBatch.add(new Object[]{
                oferta2Id,
                codigo2.toString(), 
                true, // ativa
                now,  // instanteCriacao
                "Oferta especial - Vendedor paga juros",
                maxParcelas1,
                false, // principal
                preco2,
                1,     // quemPagaJuros (1 = vendedor)
                produtoId
            });
            
            // Criar valores para parcelas seria mais complexo no JDBC puro,
            // mas por simplicidade estamos omitindo essa parte.
        }
        
        // Inserir ofertas em batch
        jdbcTemplate.batchUpdate(
            "INSERT INTO oferta (id, codigo, ativa, instante_criacao, nome, numero_maximo_parcelas, principal, preco, quem_paga_juros, produto_id) " +
            "VALUES (?, ?::uuid, ?, ?, ?, ?, ?, ?, ?, ?)",
            ofertasBatch
        );
    }
    
    /**
     * Insere uma entrada na sequência e retorna o próximo ID
     */
    private Long insertAndGetNextId(String tableName) {
        // Esta abordagem varia de acordo com o banco e pode não funcionar em todos os casos
        // Para H2, vamos usar uma abordagem simplificada
        return jdbcTemplate.queryForObject(
            "SELECT NEXT VALUE FOR " + tableName + "_seq", Long.class);
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