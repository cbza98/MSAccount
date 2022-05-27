package com.msntt.MSAccountService.infraestructure.restclient;

import com.msntt.MSAccountService.domain.beans.BusinessPartnerDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(value = "bp-service", url = "${bp.service.url}")
public interface IBusinessPartnerClient {
    @GetMapping("/BusinessPartner/{id}")
    Mono<BusinessPartnerDTO> findById(@PathVariable("id") String id);
}
