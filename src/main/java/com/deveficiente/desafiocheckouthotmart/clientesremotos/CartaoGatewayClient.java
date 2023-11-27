package com.deveficiente.desafiocheckouthotmart.clientesremotos;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentClient", url = "${gateway.payment.api.url}")
public interface CartaoGatewayClient {

    @PostMapping("/gateway/payments")
    String executa(@RequestBody NovoPagamentoGatewayCartaoRequest paymentRequest);

}
