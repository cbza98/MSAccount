package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.entities.AccountItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountItemService {

     Mono<AccountItem> save(AccountItem item);

     Mono<AccountItem> update(AccountItem item);

     Mono<AccountItem> delete(String id);

     Mono<AccountItem> findById(String id);

     Flux<AccountItem> findAll();

     Mono<AccountItem> findByAccountType(String accountType);
}
