package com.msntt.MSAccountService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@SpringBootApplication
public class MsAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAccountServiceApplication.class, args);
	}

}
