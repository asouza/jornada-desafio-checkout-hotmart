package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao;

import java.math.BigDecimal;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraCartao;
import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.NovoPagamentoGatewayCartaoRequest;

public class NovoPagamentoGatewayCartao3Request implements NovoPagamentoGatewayCartaoRequest {
	private String numeroCartao;
	private String nomeTitular;
	private String mes;
	private int anoVencimento;
	private BigDecimal valorParcela;
	private int numeroParcelas;

	public NovoPagamentoGatewayCartao3Request(String numeroCartao,
			String nomeTitular, String mes, int anoVencimento,
			BigDecimal valorParcela, int numeroParcelas) {
		super();
		this.numeroCartao = numeroCartao;
		this.nomeTitular = nomeTitular;
		this.mes = mes;
		this.anoVencimento = anoVencimento;
		this.valorParcela = valorParcela;
		this.numeroParcelas = numeroParcelas;
	}

	public NovoPagamentoGatewayCartao3Request(Compra compra) {
		
		InfoCompraCartao infoCartao = compra.getMetadados()
			.buscaInfoCompraCartao()
			.orElseThrow(() -> {
				throw new IllegalStateException("Se vai processar com cartao, precisa ter um cartao associado a compra");
			});
		
		
		this.numeroCartao = infoCartao.getNumeroCartao();
		this.nomeTitular = infoCartao.getNomeTitular();
		this.mes = infoCartao.getMes().getMesTexto();
		this.anoVencimento = infoCartao.getAnoVencimento();
		this.valorParcela = infoCartao.getValorParcela();
		this.numeroParcelas = infoCartao.getNumeroParcelas();		
	}

	@Override
	public void preencheDados(String numeroCartao, String nomeTitular,
			String mes, int anoVencimento, ValorParcelaMes valorParcelaMes) {
		this.numeroCartao = numeroCartao;
		this.nomeTitular = nomeTitular;
		this.anoVencimento = anoVencimento;
		this.numeroParcelas = valorParcelaMes.getNumeroParcelas();
		this.valorParcela = valorParcelaMes.getValor();
	}	

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public String getNomeTitular() {
		return nomeTitular;
	}

	public String getMes() {
		return mes;
	}

	public int getAnoVencimento() {
		return anoVencimento;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public int getNumeroParcelas() {
		return numeroParcelas;
	}

	@Override
	public String toString() {
		return "NovoPagamentoGatewayCartaoRequest [numeroCartao=" + numeroCartao
				+ ", nomeTitular=" + nomeTitular + ", mes=" + mes
				+ ", anoVencimento=" + anoVencimento + ", valorParcela="
				+ valorParcela + ", numeroParcelas=" + numeroParcelas + "]";
	}

}
