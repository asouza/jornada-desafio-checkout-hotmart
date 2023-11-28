package com.deveficiente.desafiocheckouthotmart.checkout;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.util.Assert;

public enum MesVencimentoCartao {

	JANEIRO("01"),
    FEVEREIRO("02"),
    MARCO("03"),
    ABRIL("04"),
    MAIO("05"),
    JUNHO("06"),
    JULHO("07"),
    AGOSTO("08"),
    SETEMBRO("09"),
    OUTUBRO("10"),
    NOVEMBRO("11"),
    DEZEMBRO("12");

	private String mesTexto;

	MesVencimentoCartao(String mesTexto) {
		this.mesTexto = mesTexto;
		// TODO Auto-generated constructor stub
	}
	
	public String getMesTexto() {
		return mesTexto;
	}

	static MesVencimentoCartao from(String mesTextoBuscado) {
		List<MesVencimentoCartao> resultado = Stream
			.of(MesVencimentoCartao.values())
			.filter(mes -> mes.mesTexto.equals(mesTextoBuscado))
			.toList();
		Assert.isTrue(resultado.size() == 1, "Deveria ter achado exatamente o mes correto para "+mesTextoBuscado);
		
		return resultado.get(0);
	}
}
