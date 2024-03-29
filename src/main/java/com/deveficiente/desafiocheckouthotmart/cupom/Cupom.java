package com.deveficiente.desafiocheckouthotmart.cupom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.compartilhado.ICP;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

@Entity
@ICP(2)
public class Cupom {

	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	@ICP
	private Produto produto;
	private @NotBlank String codigo;
	private @DecimalMin("1") @DecimalMax("99") BigDecimal percentualDesconto;
	@Future
	private LocalDateTime limiteUso;
	
	@Deprecated
	public Cupom() {
		// TODO Auto-generated constructor stub
	}

	public Cupom(Produto produto, @NotBlank String codigo,
			@DecimalMin("1") @DecimalMax("99") BigDecimal percentualDesconto,
			@Future LocalDateTime limiteUso) {
		this.produto = produto;
		this.codigo = codigo;
		this.percentualDesconto = percentualDesconto;
		this.limiteUso = limiteUso;
	}

	public BigDecimal aplicaDesconto(BigDecimal valor) {
		Assert.state(isValido(), "O cupom só pode ser aplicado se estiver válido. "+this.codigo);
		
		BigDecimal percentualRestante = new BigDecimal("100")
				.subtract(this.percentualDesconto);
		BigDecimal porcentagem = percentualRestante
				.divide(new BigDecimal("100"));

		return valor.multiply(porcentagem);
	}

	public boolean isValido() {
		//@ICP
		return LocalDateTime.now().compareTo(this.limiteUso) <= 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cupom other = (Cupom) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		return true;
	}

	public String getCodigo() {
		return this.codigo;
	}
	
	

}
