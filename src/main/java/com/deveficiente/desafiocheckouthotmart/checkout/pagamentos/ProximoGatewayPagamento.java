package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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

	private RoundRobinExecution<Compra, String> roundRobinExecution;

	public ProximoGatewayPagamento(CartaoGateway1Client cartaoGateway1Client,
			CartaoGateway2Client cartaoGateway2Client,
			CartaoGateway3Client cartaoGateway3Client) {
		super();
		
		Function<Compra, String> funcaoGateway1 = (compra) -> {
			return cartaoGateway1Client.executa(new NovoPagamentoGatewayCartao1Request(compra));				
		};
		
		Function<Compra, String>  funcaoGateway2 = (compra) -> {
			return cartaoGateway2Client.executa(new NovoPagamentoGatewayCartao2Request(compra));
		};
		
		Function<Compra, String>  funcaoGateway3 = (compra) -> {
			return cartaoGateway3Client.executa(new NovoPagamentoGatewayCartao3Request(compra));
		};
		
//		roundRobinExecution = new RoundRobinExecution<>("gatewayCartao", List.of(funcaoGateway1,funcaoGateway2,funcaoGateway3));
		roundRobinExecution = new RoundRobinExecution<>("gatewayCartao", List.of(funcaoGateway1));
	}
	
	public Supplier<String> proximoGateway(Compra compra) {
		/*
		 * tinha deixado a chamada para nextFunction dentro da função
		 * sendo retornada. Só que isso é um bug.. Se o supplier retornado
		 * fosse chamada várias vezes, ele ficaria trocando de gateway.  
		 */
		Function<Compra, String> proximoGateway = roundRobinExecution.getNextFunction();
		return () -> proximoGateway.apply(compra);
	}

}
