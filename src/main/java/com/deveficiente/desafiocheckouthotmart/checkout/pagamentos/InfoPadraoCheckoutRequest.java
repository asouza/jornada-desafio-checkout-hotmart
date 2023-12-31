package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import com.deveficiente.desafiocheckouthotmart.compartilhado.FieldsValueMatch;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.contas.Conta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@FieldsValueMatch.List({
	@FieldsValueMatch(field = "email",fieldMatch = "confirmacaoEmail")
})
public class InfoPadraoCheckoutRequest {

	@NotBlank
	private String nomeCompleto;
	@NotBlank
	@Email
	private String email;
	@NotBlank
	@Email	
	//TODO criar a annotation para validar cruzado
	private String confirmacaoEmail;	
	private String codigoCupom;

	public InfoPadraoCheckoutRequest(String nomeCompleto, String email,
			String confirmacaoEmail) {
		super();
		this.nomeCompleto = nomeCompleto;
		this.email = email;
		this.confirmacaoEmail = confirmacaoEmail;
	}
	
	public void setCodigoCupom(String codigoCupom) {
		this.codigoCupom = codigoCupom;
	}

	public String getEmail() {
		return email;
	}
	
	
	@Override
	public String toString() {
		return "InfoPadraoCheckoutRequest [nomeCompleto=" + nomeCompleto
				+ ", email=" + email + ", confirmacaoEmail=" + confirmacaoEmail
				+ ", codigoCupom=" + codigoCupom + "]";
	}

	public Conta novaConta(Configuracao configuracao) {
		return new Conta(email, configuracao);
	}
	
	

}
