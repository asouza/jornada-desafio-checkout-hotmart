package com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.checkout.InfoCompraBoleto;

public class NovoBoletoRequest {
	private String cpf;
	private String valor;
	private String dataExpiracao;
	private String codigo;

	public NovoBoletoRequest(Compra compra) {
		InfoCompraBoleto infoCompraBoleto = compra.getMetadados()
				.buscaInfoCompraBoleto().orElseThrow(() -> {
					return new IllegalStateException("Compra de codigo ="
							+ compra.getCodigo() + " não é com boleto");
				});

		this.cpf = infoCompraBoleto.getCpf();
		this.valor = infoCompraBoleto.getValor().toString();
		this.dataExpiracao = DateTimeFormatter.ofPattern("dd/MM/yyyy")
				.format(infoCompraBoleto.getDataExpiracao());
		this.codigo = infoCompraBoleto.getCodigoBoleto();
	}

	public String getCpf() {
		return cpf;
	}

	public String getValor() {
		return valor;
	}

	public String getDataExpiracao() {
		return dataExpiracao;
	}

	public String getCodigo() {
		return codigo;
	}

}
