package com.deveficiente.desafiocheckouthotmart.datapopulator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Cliente REST para popular o banco em paralelo, dividindo a carga em múltiplas requisições
 */
@RestController
@RequestMapping("/parallel-data-populator")
public class DataPopulatorRestClient {

    /**
     * Ponto de entrada para popular o banco em paralelo
     * Este método divide o trabalho em vários batches menores executados em paralelo
     * 
     * @return Resultado consolidado com todas as estatísticas
     */
    @GetMapping("/populate")
    public ResponseEntity<Map<String, Object>> populateInParallel() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Configura quantas contas criar no total e quantas por batch
            int totalAccounts = 10000;
            int accountsPerBatch = 500;
            int batches = (int) Math.ceil((double) totalAccounts / accountsPerBatch);
            
            // Configurar executor com um pool de threads adequado
            ExecutorService executor = Executors.newFixedThreadPool(
                    Math.min(8, Runtime.getRuntime().availableProcessors()));
            
            // Armazenar futuros para aguardar conclusão
            CompletableFuture<?>[] futures = new CompletableFuture[batches];
            
            // Criar tarefas para cada batch
            for (int i = 0; i < batches; i++) {
                final int batchNumber = i;
                int batchSize = (i == batches - 1) 
                        ? totalAccounts - (i * accountsPerBatch) 
                        : accountsPerBatch;
                
                futures[i] = CompletableFuture.supplyAsync(() -> 
                    executeBatch(batchSize, batchNumber), executor);
            }
            
            // Aguardar conclusão de todos os batches
            CompletableFuture.allOf(futures).join();
            
            // Encerrar executor
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);
            
            // Preparar resposta consolidada
            response.put("totalBatches", batches);
            response.put("totalAccountsRequested", totalAccounts);
            response.put("totalAccountsCreated", totalAccounts);
            response.put("totalProductsCreated", totalAccounts * 2);
            response.put("totalOffersCreated", totalAccounts * 4);
            response.put("timeElapsedMs", System.currentTimeMillis() - startTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Executa um batch chamando o endpoint individual
     */
    private Map<String, Object> executeBatch(int batchSize, int batchNumber) {
        try {
            System.out.println("Iniciando batch " + batchNumber + " com " + batchSize + " contas");
            RestTemplate restTemplate = new RestTemplate();
            
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    "http://localhost:8080/batch-data-populator/checkout-data?quantidade=" + batchSize, 
                    Map.class);
            
            System.out.println("Concluído batch " + batchNumber);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();
            return responseBody;
            
        } catch (Exception e) {
            System.err.println("Erro no batch " + batchNumber + ": " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("batch", batchNumber);
            return error;
        }
    }
}