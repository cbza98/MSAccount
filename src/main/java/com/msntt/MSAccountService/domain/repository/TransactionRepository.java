package com.msntt.MSAccountService.domain.repository;


import com.msntt.MSAccountService.domain.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collection;


public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Mono<Long> countByAccountAndTransactiontypeIn(String accountNumber,Collection<String> tType);
    Mono<Long> countByAccountAndTransactiontypeInAndCreateDateBetween(String accountNumber,
                                                                      Collection<String> tType,
                                                                      LocalDate startDate,
                                                                      LocalDate endDate);
    Mono<Long> countByCreateDateBetween(LocalDate startDay, LocalDate endDate);
    Flux<Transaction> findByAccountNumberAndCreateDateBetween(String accountId,
                                                              LocalDate startDate,
                                                              LocalDate endDate);
}
