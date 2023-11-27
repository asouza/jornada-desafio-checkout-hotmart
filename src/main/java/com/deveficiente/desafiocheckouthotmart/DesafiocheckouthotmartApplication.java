package com.deveficiente.desafiocheckouthotmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DesafiocheckouthotmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafiocheckouthotmartApplication.class, args);
	}

}
