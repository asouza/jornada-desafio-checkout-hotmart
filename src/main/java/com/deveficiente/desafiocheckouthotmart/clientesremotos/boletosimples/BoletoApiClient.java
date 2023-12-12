package com.deveficiente.desafiocheckouthotmart.clientesremotos.boletosimples;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "boletoClient", url = "${boleto.payment.api.url}")
public interface BoletoApiClient {

    @PostMapping("/boletos/new")
    String executa(@RequestBody NovoBoletoRequest paymentRequest);

}
