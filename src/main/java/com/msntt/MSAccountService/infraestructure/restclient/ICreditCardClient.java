package com.msntt.MSAccountService.infraestructure.restclient;

import com.msntt.MSAccountService.domain.beans.CreditCardCountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(value = "credit-card-service", url = "${bp.service.url}")
public interface ICreditCardClient {
    @GetMapping("/countByBusinessPartner/{id}")
    Mono<CreditCardCountDTO> countCreditCardsByBusinessPartner(@PathVariable("id") String idBusinessPartner);
}
