# Desafio Checkout Hotmart - Setup e Testes

## Passos para subir o projeto

1. **Execute o projeto:**
```bash
./mvnw spring-boot:run
```

2. **Popule o banco de dados com dados de teste:**
```bash
curl -X POST http://localhost:8080/data-populator/setup-data
```

3. **Liste os produtos para obter códigos de ofertas:**
```bash
curl http://localhost:8080/api/produtos
```

4. **Execute um pagamento com cartão de crédito:**
```bash
curl -X POST http://localhost:8080/checkouts/produtos/{codigoProduto}/{codigoOferta} \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $(uuidgen)" \
  -d '{
    "email": "comprador@email.com",
    "dadosCartao": {
      "numero": "4111111111111111",
      "titular": "João Silva",
      "mesVencimento": 12,
      "anoVencimento": 2025,
      "codigoSeguranca": "123"
    }
  }'
```

## Observações
- Substitua `{codigoProduto}` e `{codigoOferta}` pelos UUIDs obtidos na listagem de produtos
- O comando `$(uuidgen)` gera automaticamente uma chave de idempotência única
- A aplicação estará disponível em `http://localhost:8080`