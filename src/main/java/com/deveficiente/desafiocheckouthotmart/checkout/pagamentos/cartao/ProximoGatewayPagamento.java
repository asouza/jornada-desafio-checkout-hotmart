package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.deveficiente.desafiocheckouthotmart.featureflag.FeatureFlagService;
import org.springframework.stereotype.Component;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGateway1Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao.CartaoGateway2Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao.NovoPagamentoGatewayCartao2Request;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao.CartaoGateway3Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao.NovoPagamentoGatewayCartao3Request;
import com.deveficiente.desafiocheckouthotmart.compartilhado.RoundRobinExecution;
import com.deveficiente.desafiocheckouthotmart.ofertas.Oferta;

@Component
public class ProximoGatewayPagamento {

	/**
	 * crio uma lista de opcoes de funcoes... A opcao é definida pela feature flag
	 * enquanto a proxima funcao nao existir, eu pego a proxima.
	 * eu tenho que fazer isso, pq se eu resolvo apenas no startup, não adianta desabilitar a feature em producao
	 *
	 */

	private RoundRobinExecution<Compra, String> roundRobinExecution;
	private FeatureFlagService featureFlagService;

	public ProximoGatewayPagamento(CartaoGateway1Client cartaoGateway1Client,
			CartaoGateway2Client cartaoGateway2Client,
			CartaoGateway3Client cartaoGateway3Client,
			FeatureFlagService featureFlagService) {
		super();
        this.featureFlagService = featureFlagService;
        Function<Compra, String> funcaoGateway1 = (compra) -> {
			return cartaoGateway1Client.executa(new NovoPagamentoGatewayCartao1Request(compra));				
		};
		
		Function<Compra, String>  funcaoGateway2 = (compra) -> {
			return cartaoGateway2Client.executa(new NovoPagamentoGatewayCartao2Request(compra));
		};
		
		Function<Compra, String>  funcaoGateway3 = (compra) -> {
			return cartaoGateway3Client.executa(new NovoPagamentoGatewayCartao3Request(compra));
		};

		//aqui eu preciso postergar a execucao, para realizar a checagem de feature flag com a aplicacao ja rodando.
		Supplier<Optional<Function<Compra, String>>> possivelGateway1 = () -> featureFlagService.optionalizeFeature("gateway1",funcaoGateway1);
		Supplier<Optional<Function<Compra, String>>> possivelGateway2 = () -> featureFlagService.optionalizeFeature("gateway2",funcaoGateway2);
		Supplier<Optional<Function<Compra, String>>> possivelGateway3 = () -> featureFlagService.optionalizeFeature("gateway3",funcaoGateway3);
		roundRobinExecution = new RoundRobinExecution<>("gatewayCartao", List.of(possivelGateway1,possivelGateway2,possivelGateway3));
	}
	
	public Supplier<String> proximoGateway(Compra compra) {
		/*
		 * tinha deixado a chamada para nextFunction dentro da função
		 * sendo retornada. Só que isso é um bug.. Se o supplier retornado
		 * fosse chamada várias vezes, ele ficaria trocando de gateway.
		 */

//		logica adicional para descobrir proximo gateway habilitado

		Optional<Function<Compra, String>> proximoGateway;
		int indiceOpcaoAtual = 0;
		int limite = roundRobinExecution.getNumberOfOptions();

		//enquanto nao chegou no limite e ainda nao encontrou um gateway disponivel, continua
		//talvez aqui seja um gargalo de execucao, confesso que neste momento nao sei. 
		while((proximoGateway = roundRobinExecution.getNextFunction().get()).isEmpty() && indiceOpcaoAtual < limite){
			indiceOpcaoAtual++;
		}
		/**
		 * Sem essa linha, eu recebia isso aqui:
		 * java: local variables referenced from a lambda expression must be final or effectively final
		 */
		Optional<Function<Compra, String>> proximoGatewayFinal = proximoGateway;

		return () -> proximoGatewayFinal.
				map(funcao -> funcao.apply(compra))
				.orElseThrow(() -> new IllegalStateException("Aparentemente ficamos sem opcoes de gateways para executar. Será que todos foram desabilitados?"));
	}

}
