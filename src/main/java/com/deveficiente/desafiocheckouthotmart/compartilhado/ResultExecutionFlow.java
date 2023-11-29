package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.util.Assert;

/**
 * Used with {@link Result}
 * @author albertoluizsouza
 *
 * @param <FinalTypeReturn>
 * @param <ProblemType>
 */
public class ResultExecutionFlow<FinalTypeReturn,ProblemType> {

	private FinalTypeReturn finalReturn;
	private ProblemType problemReturn;

	public ResultExecutionFlow(FinalTypeReturn retornoSucesso, ProblemType retornoProblema) {
		this.finalReturn = retornoSucesso;
		this.problemReturn = retornoProblema;
	}

	public static <TipoRetornoFinal,TipoProblema> ResultExecutionFlow<TipoRetornoFinal,TipoProblema> success(TipoRetornoFinal retornoSucesso) {
		return new ResultExecutionFlow<TipoRetornoFinal,TipoProblema>(retornoSucesso,null);
	}
	
	public static <TipoRetornoFinal,TipoProblema> ResultExecutionFlow<TipoRetornoFinal,TipoProblema> problem(TipoProblema retornoProblema) {
		return new ResultExecutionFlow<TipoRetornoFinal,TipoProblema>(null,retornoProblema);
	}

	@SuppressWarnings("unchecked")
	public <ExceptionType> ResultExecutionFlow<FinalTypeReturn,ProblemType> throwsEarlyIf(Class<ExceptionType> classeProblema,
			Function<ExceptionType, ? extends Exception> funcao) throws Exception {
		if(problemReturn != null && problemReturn.getClass().equals(classeProblema)) {
			throw funcao.apply((ExceptionType)this.problemReturn);
		}
		
		return this;
	}

	public Optional<FinalTypeReturn> execute() {
		return Optional.ofNullable(this.finalReturn);
	}

	public ResultExecutionFlow<FinalTypeReturn,ProblemType> ifProblem(
			Class<?> classeProblema, Function<ProblemType, FinalTypeReturn> funcao) {
		
		if(problemReturn != null && problemReturn.getClass().equals(classeProblema)) {
			Assert.isTrue(Objects.isNull(this.finalReturn), "O retorno final não deveria estar setado ainda. "+this.finalReturn);
			this.finalReturn = funcao.apply(this.problemReturn);
		}
		return this;
	}	

}
