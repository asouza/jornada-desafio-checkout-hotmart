package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentClient", url = "${gateway.payment.api.url}")
public interface CartaoGateway1Client {

    @PostMapping("/gateway/payments")
    String executa(@RequestBody NovoPagamentoGatewayCartao1Request paymentRequest);

}
