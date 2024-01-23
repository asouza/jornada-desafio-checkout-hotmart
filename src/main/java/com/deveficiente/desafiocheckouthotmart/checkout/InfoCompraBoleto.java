package com.deveficiente.desafiocheckouthotmart.checkout;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.validator.constraints.UUID;
import org.hibernate.validator.constraints.br.CPF;

import com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.boleto.ConfiguracaoBoleto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class InfoCompraBoleto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CPF
	@NotBlank
	private String cpf;
	@UUID
	@NotBlank
	private String codigoBoleto;
	@Positive
	@NotNull
	private BigDecimal valor;
	@FutureOrPresent
	@NotNull
	private LocalDate dataExpiracao;
	@OneToOne
	private MetadadosCompra metadadosCompra;

	@Deprecated
	public InfoCompraBoleto() {
		// TODO Auto-generated constructor stub
	}

	public InfoCompraBoleto(MetadadosCompra metadadosCompra, String cpf, ConfiguracaoBoleto configuracaoBoleto) {
		this.metadadosCompra = metadadosCompra;
		this.cpf = cpf;
		this.codigoBoleto = configuracaoBoleto.geraCodigoParaBoleto();
		this.valor = metadadosCompra.getCompra().getPrecoFinal();
		this.dataExpiracao = configuracaoBoleto.dataExpiracao(LocalDate.now());		
	}

	public String getCpf() {
		return cpf;
	}

	public String getCodigoBoleto() {
		return codigoBoleto;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public LocalDate getDataExpiracao() {
		return dataExpiracao;
	}

}
