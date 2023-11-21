package com.deveficiente.desafiocheckouthotmart.compartilhado;

import org.springframework.util.Assert;

import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

public class Resultado<TipoProblema extends RuntimeException, TipoSucesso> {

    private boolean sucesso;
    private TipoProblema problema;
    private TipoSucesso retorno;
    
    private Resultado(TipoSucesso retorno) {
		this.retorno = retorno;
		this.sucesso = true;
    }    

    private Resultado(boolean sucesso) {
        this.sucesso = sucesso;
    }

    private Resultado(TipoProblema problema) {
        this.problema = problema;
    }

    public static Resultado<RuntimeException, Void> sucessoSemInfoAdicional() {
        return new Resultado(true);
    }

    public static <TipoProblema extends RuntimeException,TipoRetorno> Resultado<TipoProblema, TipoRetorno> falhaCom(TipoProblema problema) {
        return new Resultado<TipoProblema,TipoRetorno>(problema);
    }

	public boolean temErro() {
		return !this.sucesso;
	}

	public RuntimeException getProblema() {
		Assert.isTrue(!sucesso, "Só pode buscar o problema se tiver erro");
		return this.problema;
	}

	public boolean isSucesso() {
		return this.sucesso;
	}

	public static <T> Resultado<RuntimeException,T> sucessoComInfoAdicional(T retorno) {
		return new Resultado<RuntimeException,T>(retorno);
	}


}
