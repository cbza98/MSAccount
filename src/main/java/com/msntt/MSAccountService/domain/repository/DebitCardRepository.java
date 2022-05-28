package com.msntt.MSAccountService.domain.repository;

import com.msntt.MSAccountService.domain.model.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard,String> {
    Mono<DebitCard> findByCardNumberAndExpiringDateAndCvv(String creditCardNumber,String expDate,String cvv);
}
