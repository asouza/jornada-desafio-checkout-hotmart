package com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentClient2", url = "${gateway.payment.api.url}")
public interface CartaoGateway2Client {

    @PostMapping("/gateway-2/payments")
    String executa(@RequestBody NovoPagamentoGatewayCartao2Request paymentRequest);

}
