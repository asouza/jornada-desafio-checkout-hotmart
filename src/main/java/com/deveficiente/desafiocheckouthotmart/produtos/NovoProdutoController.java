package com.deveficiente.desafiocheckouthotmart.produtos;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deveficiente.desafiocheckouthotmart.compartilhado.BindExceptionFactory;
import com.deveficiente.desafiocheckouthotmart.compartilhado.Result;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.ContaRepository;
import com.deveficiente.desafiocheckouthotmart.contas.JaExisteProdutoComMesmoNomeException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

//TODO tests
@RestController
public class NovoProdutoController {
    
    private ContaRepository contaRepository;

    public NovoProdutoController(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @PostMapping("/contas/{codigoConta}/produtos")
    @Transactional
    public ResponseEntity<String> criar(@PathVariable("codigoConta") String codigoConta , @RequestBody @Valid NovoProdutoRequest request) throws Exception {
        Conta conta = contaRepository.findByCodigo(UUID.fromString(codigoConta))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));

        Result<RuntimeException, Produto> resultado = conta.adicionaProduto(request :: toModel);
        
        //#naoSeiPq aqui ele precisa que salve para de fato aplicar o cascade.
        //contaRepository.save(conta);
        
        //#possivelResposta Configurando o cascade como persist foi. Então alteração de estado no objeto dentro de uma transacao triga o persist?

       
        return resultado
        	.ifSuccess(produto -> {
        		return ResponseEntity.ok(produto.getCodigo().toString());
        	})
        	.throwsEarlyIf(JaExisteProdutoComMesmoNomeException.class, erro -> {
            	return BindExceptionFactory.createGlobalError(request, "novoProdutoRequest"
            			,"Já existe um produto com mesmo nome para esta conta. "+erro.getProduto().getNome());
        	})
        	.execute().get();

    }
}
