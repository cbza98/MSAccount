package com.msntt.MSAccountService.domain.repository;


import com.msntt.MSAccountService.domain.beans.FeeCharged;
import com.msntt.MSAccountService.domain.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;


public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Mono<Long> countByAccountAndTransactiontypeIn(String accountNumber,Collection<String> tType);
    Mono<Long> countByAccountAndTransactiontypeInAndCreateDateBetween(String accountNumber,
                                                                      Collection<String> tType,
                                                                      LocalDate startDate,
                                                                      LocalDate endDate);
    Flux<Transaction> findByDebitCardIdOrderByCreateDateDesc(String debitCardNumber);

    Flux<FeeCharged> findByAccountAndCreateDateBetweenAndCommissionAmountGreaterThan(String accountNumber,
                                                                                     LocalDate startDate,
                                                                                     LocalDate endDat,
                                                                                     BigDecimal limit);


}
