package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentClient3", url = "${gateway.payment.api.url}")
public interface CartaoGateway3Client {

    @PostMapping("/gateway-3/payments")
    String executa(@RequestBody NovoPagamentoGatewayCartao3Request paymentRequest);

}
