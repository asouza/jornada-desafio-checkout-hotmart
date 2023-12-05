package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao;

import java.math.BigDecimal;

import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraCartao;

public class NovoPagamentoGatewayCartao2Request {
	private String numeroCartao;
	private String nomeTitular;
	private String mes;
	private int anoVencimento;
	private BigDecimal valorParcela;
	private int numeroParcelas;

	public NovoPagamentoGatewayCartao2Request(String numeroCartao,
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

	public InfoCompraCartao toInfoCompraCartao() {
		return new InfoCompraCartao(this.numeroCartao,this.nomeTitular,this.valorParcela,this.numeroParcelas,this.anoVencimento,this.mes);
	}

}
