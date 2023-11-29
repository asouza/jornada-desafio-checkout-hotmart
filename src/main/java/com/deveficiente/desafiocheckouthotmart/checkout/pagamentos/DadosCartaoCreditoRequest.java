package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.deveficiente.desafiocheckouthotmart.checkout.MesVencimentoCartao;
import com.deveficiente.desafiocheckouthotmart.checkout.ValorParcelaMes;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartaoRequest;
import com.deveficiente.desafiocheckouthotmart.compartilhado.FutureOrPresentYear;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DadosCartaoCreditoRequest {

	@NotBlank
	@CreditCardNumber
	private String numeroCartao;
	@NotBlank
	private String nomeTitular;
	@NotNull
	private MesVencimentoCartao mes;
	// usando integer aqui para garantir que nao vem nulo
	@Positive
	@FutureOrPresentYear
	@NotNull
	private Integer anoVencimento;
	@Positive
	@NotNull
	private Integer numeroParcelas;

	public DadosCartaoCreditoRequest(
			@NotBlank @CreditCardNumber String numeroCartao,
			@NotBlank String nomeTitular, @NotNull MesVencimentoCartao mes,
			@Positive @FutureOrPresentYear @NotNull Integer anoVencimento,
			@Positive @NotNull Integer numeroParcelas) {
		super();
		this.numeroCartao = numeroCartao;
		this.nomeTitular = nomeTitular;
		this.mes = mes;
		this.anoVencimento = anoVencimento;
		this.numeroParcelas = numeroParcelas;
	}

	@Override
	public String toString() {
		return "DadosCartaoCreditoRequest [numeroCartao=" + numeroCartao
				+ ", nomeTitular=" + nomeTitular + ", mes=" + mes
				+ ", anoVencimento=" + anoVencimento + ", numeroParcelas="
				+ numeroParcelas + "]";
	}

	public NovoPagamentoGatewayCartaoRequest toPagamentoGatewayCartaoRequest(
			Oferta oferta) {
		ValorParcelaMes parcelaMes = oferta
				.getValorParcelaParaDeterminadoNumero(this.numeroParcelas);

		return new NovoPagamentoGatewayCartaoRequest(numeroCartao, nomeTitular,
				mes.getMesTexto(), anoVencimento, parcelaMes.getValor(),
				parcelaMes.getNumeroParcelas());
	}

}
