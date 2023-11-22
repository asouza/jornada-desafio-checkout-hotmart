package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.function.Function;

import org.springframework.util.Assert;

public class Result<ProblemType extends RuntimeException, SuccessType> {

    private boolean isSuccess;
    private ProblemType problem;
    private SuccessType successReturnObject;
    
    private Result(SuccessType successReturnObject) {
		this.successReturnObject = successReturnObject;
		this.isSuccess = true;
    }    

    private Result(boolean success) {
        this.isSuccess = success;
    }

    private Result(ProblemType problem) {
        this.problem = problem;
    }

    public static Result<RuntimeException, Void> emptySuccess() {
        return new Result<RuntimeException,Void>(true);
    }

    public static <TipoProblema extends RuntimeException,TipoRetorno> Result<TipoProblema, TipoRetorno> failWithProblem(TipoProblema problema) {
        return new Result<TipoProblema,TipoRetorno>(problema);
    }

	public boolean hasError() {
		return !this.isSuccess;
	}

	public RuntimeException getProblem() {
		Assert.isTrue(!isSuccess, "Só pode buscar o problema se tiver erro");
		return this.problem;
	}

	public boolean isSuccess() {
		return this.isSuccess;
	}

	public static <T> Result<RuntimeException,T> successWithReturn(T successReturn) {
		return new Result<RuntimeException,T>(successReturn);
	}

	public <T> ResultExecutionFlow<T,ProblemType> ifSuccess(Function<SuccessType, T> funcao) {
		if(isSuccess()) {
			return ResultExecutionFlow.success(funcao.apply(this.successReturnObject));
		}
		return ResultExecutionFlow.problem(this.problem);
	}


}
