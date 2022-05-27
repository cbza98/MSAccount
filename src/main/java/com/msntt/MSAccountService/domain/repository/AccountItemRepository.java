package com.msntt.MSAccountService.domain.repository;

import com.msntt.MSAccountService.domain.model.AccountItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccountItemRepository extends ReactiveMongoRepository<AccountItem,String> {
    Mono<AccountItem> findByAccountType(String accountType);
}
