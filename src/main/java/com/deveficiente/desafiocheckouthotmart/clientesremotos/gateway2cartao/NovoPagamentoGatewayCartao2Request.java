package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao;

import java.math.BigDecimal;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraCartao;
import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;
import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao.NovoCheckoutCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.NovoPagamentoGatewayCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

public class NovoPagamentoGatewayCartao2Request implements NovoPagamentoGatewayCartaoRequest {
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

	public NovoPagamentoGatewayCartao2Request(Compra compra) {
		
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

	@Override
	public void preencheDados(String numeroCartao, String nomeTitular,
			String mes, int anoVencimento, ValorParcelaMes valorParcelaMes) {
		this.numeroCartao = numeroCartao;
		this.nomeTitular = nomeTitular;
		this.anoVencimento = anoVencimento;
		this.numeroParcelas = valorParcelaMes.getNumeroParcelas();
		this.valorParcela = valorParcelaMes.getValor();
	}

}
