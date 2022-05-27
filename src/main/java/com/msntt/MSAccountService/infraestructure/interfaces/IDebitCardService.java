package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.beans.*;
import com.msntt.MSAccountService.domain.model.DebitCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDebitCardService {
    Flux<DebitCard> findAll();

    Mono<DebitCard> createDebitCard(CreateDebitCardDTO debitCardDTO);

    Mono<DebitCard> delete(String id);

    Mono<DebitCard> findById(String id);

    Mono<DebitCard> associateAccount(AssociateAccountDTO accountDTO);

}
