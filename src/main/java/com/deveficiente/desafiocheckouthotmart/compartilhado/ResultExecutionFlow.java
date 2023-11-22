package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.Objects;
import java.util.function.Function;

import org.springframework.util.Assert;

public class FluxoExecucaoResultado<TipoSucesso,TipoProblema> {

	private TipoSucesso retornoSucesso;
	private TipoProblema retornoProblema;

	public FluxoExecucaoResultado(TipoSucesso retornoSucesso, TipoProblema retornoProblema) {
		this.retornoSucesso = retornoSucesso;
		this.retornoProblema = retornoProblema;
	}

	public static <TipoSucesso,TipoProblema> FluxoExecucaoResultado<TipoSucesso,TipoProblema> sucesso(TipoSucesso retornoSucesso) {
		return new FluxoExecucaoResultado<TipoSucesso,TipoProblema>(retornoSucesso,null);
	}
	
	public static <TipoSucesso,TipoProblema> FluxoExecucaoResultado<TipoSucesso,TipoProblema> problema(TipoProblema retornoProblema) {
		return new FluxoExecucaoResultado<TipoSucesso,TipoProblema>(null,retornoProblema);
	}

	public FluxoExecucaoResultado<TipoSucesso,TipoProblema> throwsIf(Class<?> classeProblema,
			Function<TipoProblema, ? extends Exception> funcao) throws Exception {
		if(retornoProblema != null && retornoProblema.getClass().equals(classeProblema)) {
			throw funcao.apply(this.retornoProblema);
		}
		
		return this;
	}

	public TipoSucesso executa() {
		Assert.isTrue(Objects.nonNull(this.retornoSucesso), "Neste momento a execucao final só deve acontecer quando tem um retorno de sucesso");
		return this.retornoSucesso;
	}	

}
