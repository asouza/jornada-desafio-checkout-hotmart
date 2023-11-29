package com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "provider1EmailClient", url = "${provider1.emails.api.url}")
public interface Provider1EmailClient {

	@PostMapping("/provider1/emails")
	String sendEmail(@RequestBody Provider1EmailRequest emailRequest);

}
