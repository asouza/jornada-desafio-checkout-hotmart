package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao.PagaComCartaoCreditoController;
import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.compartilhado.PartialClass;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.configuracoes.ConfiguracaoRepository;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;
import com.deveficiente.desafiocheckouthotmart.contas.ContaRepository;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;
import com.deveficiente.desafiocheckouthotmart.produtos.ProdutoRepository;

@Component
@ICP(6)
@PartialClass(PagaComCartaoCreditoController.class)
public class BuscasNecessariasParaPagamento {

	@ICP
	private ContaRepository contaRepository;
	@ICP
	private ConfiguracaoRepository configuracaoRepository;
	@ICP
	private ProdutoRepository produtoRepository;

	public BuscasNecessariasParaPagamento(@ICP ContaRepository contaRepository,
			@ICP ConfiguracaoRepository configuracaoRepository,
			@ICP ProdutoRepository produtoRepository) {
		super();
		this.contaRepository = contaRepository;
		this.configuracaoRepository = configuracaoRepository;
		this.produtoRepository = produtoRepository;
	}

	@ICP
	public Optional<Conta> findContaByEmail(String email) {
		return contaRepository.findByEmail(email);
	}

	public @ICP Configuracao getConfiguracaoDefault() {
		return configuracaoRepository.getByOpcaoDefaultIsTrue();
	}

	@ICP
	public Optional<Produto> buscaProdutoPorCodigo(String codigoProduto) {
		return produtoRepository.findByCodigo(UUID.fromString(codigoProduto));
	}

}
