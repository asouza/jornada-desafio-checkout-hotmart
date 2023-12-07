package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao;

import java.math.BigDecimal;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraCartao;

public class NovoPagamentoGatewayCartao1Request {
	private String numeroCartao;
	private String nomeTitular;
	private String mes;
	private int anoVencimento;
	private BigDecimal valorParcela;
	private int numeroParcelas;

	

	public NovoPagamentoGatewayCartao1Request(Compra compra) {
		
		/*
		 * Inicialmente eu recebia a request e oferta. Mas, no meu sistema,
		 * um compra deve sempre criada no começo do pagamento. Então eu forço
		 * todo mundo a receber tal compra. 
		 * 
		 * Só percebi implementando mesmo... Na imaginação pareceu fazer sentido 
		 * o outro jeito. Aí eu tinha usado uma politica de double dispatch, criando
		 * uma interface que todo dto de saida de gateway de pagamento deveria implementar
		 * e que seria chamado lá pelo request de pagamento com cartao
		 */
		
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

	public InfoCompraCartao toInfoCompraCartao() {
		return new InfoCompraCartao(this.numeroCartao,this.nomeTitular,this.valorParcela,this.numeroParcelas,this.anoVencimento,this.mes);
	}





}
