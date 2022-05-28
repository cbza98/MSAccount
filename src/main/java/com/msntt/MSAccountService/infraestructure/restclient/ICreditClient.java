package com.msntt.MSAccountService.infraestructure.restclient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(value = "credit-service", url = "${cc.service.url}")
public interface ICreditClient {
    @GetMapping("/Entities/CreditCard/countByBusinessPartner/{id}")
    Mono<Long> countCreditCardsByBusinessPartner(@PathVariable("id") String idBusinessPartner);
    @GetMapping("/Services/Transaction/Expireddebit/{id}")
    Mono<Long> getExpiredDebts(@PathVariable("id") String idBusinessPartner);
}
